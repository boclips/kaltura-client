package com.boclips.kalturaclient


import com.boclips.kalturaclient.captionasset.CaptionAsset
import com.boclips.kalturaclient.captionasset.CaptionFormat
import com.boclips.kalturaclient.captionasset.KalturaLanguage
import com.boclips.kalturaclient.media.MediaEntry
import com.boclips.kalturaclient.media.MediaEntryStatus
import org.apache.commons.io.IOUtils
import org.yaml.snakeyaml.Yaml
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

class KalturaClientContractTest extends Specification {

    String referenceIdOne
    String referenceIdTwo

    void setup() {
        referenceIdOne = UUID.randomUUID().toString()
        referenceIdTwo = UUID.randomUUID().toString()
        cleanup()
    }

    void cleanup() {
        tryDeleteMediaEntry(referenceIdOne)
        tryDeleteMediaEntry(referenceIdTwo)
    }

    void tryDeleteMediaEntry(String referenceId) {
        try {
            realClient().deleteMediaEntriesByReferenceId(referenceId)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    def "create and delete media entries"(KalturaClient client) {
        given:
        def referenceId = UUID.randomUUID().toString()
        client.createMediaEntry(referenceId)
        List<MediaEntry> createdMediaEntry = client.getMediaEntriesByReferenceId(referenceId)

        when:
        client.deleteMediaEntriesByReferenceId(referenceId)
        List<MediaEntry> deletedMediaEntry = client.getMediaEntriesByReferenceId(referenceId)

        then:
        createdMediaEntry.size() == 1
        createdMediaEntry[0].referenceId == referenceId
        deletedMediaEntry.isEmpty()

        where:
        client << [realClient(), testClient()]
    }

    def "delete media entries by entryId"(KalturaClient client) {
        given:
        def referenceId = UUID.randomUUID().toString()
        client.createMediaEntry(referenceId)
        List<MediaEntry> createdMediaEntries = client.getMediaEntriesByReferenceId(referenceId)

        when:
        client.deleteMediaEntryById(createdMediaEntries.get(0).id)
        List<MediaEntry> deletedMediaEntry = client.getMediaEntriesByReferenceId(referenceId)

        then:
        createdMediaEntries.size() == 1
        createdMediaEntries[0].referenceId == referenceId
        deletedMediaEntry.isEmpty()

        where:
        client << [realClient(), testClient()]
    }


    def "fetch media entries from api by reference id"(KalturaClient client) {
        given:
        client.createMediaEntry(referenceIdOne)

        when:
        Map<String, List<MediaEntry>> mediaEntries = client.getMediaEntriesByReferenceIds([
                referenceIdOne,
                "unknown-reference-id"
        ])

        then:
        mediaEntries.size() == 1

        MediaEntry mediaEntry = mediaEntries[referenceIdOne][0]
        !mediaEntry.id.isEmpty()
        mediaEntry.referenceId == referenceIdOne
        mediaEntry.duration != null
        mediaEntry.downloadUrl.contains('/entryId/' + mediaEntry.id)
        mediaEntry.downloadUrl.contains('/format/download')
        mediaEntry.getStatus() == MediaEntryStatus.NOT_READY

        where:
        client << [realClient(), testClient()]
    }

    def "fetch media entries from api by entry ids"(KalturaClient client) {
        given:
        client.createMediaEntry(referenceIdOne)

        when:
        Map<String, List<MediaEntry>> mediaEntries = client.getMediaEntriesByReferenceIds([
                referenceIdOne
        ])

        then:
        mediaEntries.size() == 1
        mediaEntries[referenceIdOne].size().equals(1)
        MediaEntry mediaEntry = mediaEntries[referenceIdOne][0]

        MediaEntry mediaEntryById = client.getMediaEntriesByIds([mediaEntry.id]).get(mediaEntry.id)

        mediaEntry.id.equals(mediaEntryById.id)
        mediaEntry.referenceId.equals(mediaEntryById.referenceId)

        where:
        client << [realClient(), testClient()]
    }

    def "fetch media entries from api by entry id"(KalturaClient client) {
        given:
        client.createMediaEntry(referenceIdOne)

        when:
        Map<String, List<MediaEntry>> mediaEntries = client.getMediaEntriesByReferenceIds([
                referenceIdOne
        ])

        then:
        mediaEntries.size() == 1
        mediaEntries[referenceIdOne].size().equals(1)
        MediaEntry mediaEntry = mediaEntries[referenceIdOne][0]

        MediaEntry mediaEntryById = client.getMediaEntryById(mediaEntry.id)

        mediaEntry.id.equals(mediaEntryById.id)
        mediaEntry.referenceId.equals(mediaEntryById.referenceId)

        where:
        client << [realClient(), testClient()]
    }

    def "create and list caption files by reference id"(KalturaClient client) {
        given:
        client.createMediaEntry(referenceIdOne)
        client.createMediaEntry(referenceIdTwo)

        when:
        CaptionAsset captionAsset = CaptionAsset.builder()
                .label("English (auto-generated)")
                .language(KalturaLanguage.ENGLISH)
                .fileType(CaptionFormat.WEBVTT)
                .build()
        client.createCaptionsFile(referenceIdOne, captionAsset, readResourceFile("/captions.vtt"))
        List<CaptionAsset> emptyCaptions = client.getCaptionFilesByReferenceId(referenceIdTwo)
        List<CaptionAsset> captions = client.getCaptionFilesByReferenceId(referenceIdOne)
        List<String> contents = captions.stream()
                .map { caption -> caption.id }
                .map { id -> client.getCaptionContentByAssetId(id) }
                .collect(Collectors.toList())

        then:
        emptyCaptions.size() == 0
        captions.size() == 1
        captions.first().id.length() > 0
        captions.first().label == "English (auto-generated)"
        captions.first().language == KalturaLanguage.ENGLISH
        captions.first().fileType == CaptionFormat.WEBVTT

        then:
        contents.size() == 1
        contents.first().contains("Tintern Abbey")

        where:
        client << [realClient(), testClient()]
    }

    def "create and list caption files by entry id"(KalturaClient client) {
        given:
        client.createMediaEntry(referenceIdOne)
        MediaEntry mediaEntry = client.getMediaEntriesByReferenceId(referenceIdOne).get(0)

        when:
        CaptionAsset captionAsset = CaptionAsset.builder()
                .label("English (auto-generated)")
                .language(KalturaLanguage.ENGLISH)
                .fileType(CaptionFormat.WEBVTT)
                .build()
        client.createCaptionsFileWithEntryId(mediaEntry.id, captionAsset, readResourceFile("/captions.vtt"))
        List<CaptionAsset> captions = client.getCaptionFilesByEntryId(mediaEntry.id)
        List<String> contents = captions.stream()
                .map { caption -> caption.id }
                .map { id -> client.getCaptionContentByAssetId(id) }
                .collect(Collectors.toList())

        then:
        captions.size() == 1
        captions.first().id.length() > 0
        captions.first().label == "English (auto-generated)"
        captions.first().language == KalturaLanguage.ENGLISH
        captions.first().fileType == CaptionFormat.WEBVTT

        then:
        contents.size() == 1
        contents.first().contains("Tintern Abbey")

        where:
        client << [realClient(), testClient()]
    }

    def "delete caption files"(KalturaClient client) {
        given:
        client.createMediaEntry(referenceIdOne)
        CaptionAsset captionAsset = CaptionAsset.builder()
                .label("English (auto-generated)")
                .language(KalturaLanguage.ENGLISH)
                .fileType(CaptionFormat.WEBVTT)
                .build()
        String assetId = client.createCaptionsFile(referenceIdOne, captionAsset, readResourceFile("/captions.vtt")).id

        when:
        client.deleteCaptionContentByAssetId(assetId)
        List<CaptionAsset> assets = client.getCaptionFilesByReferenceId(referenceIdOne)

        then:
        assets.isEmpty()

        where:
        client << [realClient(), testClient()]
    }

    def "can tag base entries"() {
        given:
        client.createMediaEntry(referenceIdOne)
        Map<String, List<MediaEntry>> mediaEntries = client.getMediaEntriesByReferenceIds([
                referenceIdOne,
        ])
        String entryId = mediaEntries.get(referenceIdOne)[0].id

        when:
        client.tag(entryId, ["just", "testing"])

        then:
        client.getBaseEntry(entryId).tags == ["just", "testing"]

        where:
        client << [testClient(), realClient()]
    }

    private static KalturaClient testClient() {
        new TestKalturaClient()
    }

    private KalturaClient realClient() {
        Map<String, String> configuration = readConfiguration()
        KalturaClientConfig config = KalturaClientConfig.builder()
                .partnerId(configuration.get("PARTNER_ID"))
                .userId(configuration.get("USER_ID"))
                .secret(configuration.get("SECRET"))
                .streamFlavorParamIds(configuration.get("FLAVOR_PARAM_IDS"))
                .build()

        return KalturaClient.create(config)
    }

    private Map<String, String> readConfiguration() {
        Map<String, String> configuration = new HashMap<>()

        Yaml yaml = new Yaml()
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("contract-test-setup.yml")

        if (inputStream != null) {
            configuration = yaml.load(inputStream)
            inputStream.close()
        }

        if (System.getenv("PARTNER_ID") != null) {
            configuration["PARTNER_ID"] = System.getenv("PARTNER_ID")
        }
        if (System.getenv("USER_ID") != null) {
            configuration["USER_ID"] = System.getenv("USER_ID")
        }
        if (System.getenv("SECRET") != null) {
            configuration["SECRET"] = System.getenv("SECRET")
        }
        if (System.getenv("FLAVOR_PARAM_IDS") != null) {
            configuration["FLAVOR_PARAM_IDS"] = System.getenv("FLAVOR_PARAM_IDS")
        }

        return configuration
    }

    private String readResourceFile(String path) {
        InputStream is = null
        try {
            is = KalturaClientContractTest.class.getResourceAsStream(path)
            return IOUtils.readLines(is, StandardCharsets.UTF_8)
                    .stream()
                    .collect(Collectors.joining("\n"))
        } catch (IOException e) {
            throw new RuntimeException(e)
        }
        finally {
            if (is != null) {
                is.close()
            }
        }
    }
}
