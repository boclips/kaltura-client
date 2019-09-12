package com.boclips.kalturaclient


import com.boclips.kalturaclient.captionasset.CaptionAsset
import com.boclips.kalturaclient.captionasset.CaptionFormat
import com.boclips.kalturaclient.captionasset.KalturaLanguage
import com.boclips.kalturaclient.media.MediaEntry
import com.boclips.kalturaclient.media.MediaEntryStatus
import com.boclips.kalturaclient.media.streams.StreamFormat
import org.apache.commons.io.IOUtils
import org.yaml.snakeyaml.Yaml
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

class KalturaClientContractTest extends Specification {

    void setup() {
        cleanup()
    }

    void cleanup() {
        tryDeleteMediaEntry("test-reference-id")
        tryDeleteMediaEntry("another-test-reference-id")
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

    def "fetch media entries from api"(KalturaClient client) {
        given:
        client.createMediaEntry("test-reference-id")

        when:
        Map<String, List<MediaEntry>> mediaEntries = client.getMediaEntriesByReferenceIds([
                "test-reference-id",
                "unknown-reference-id"
        ])

        then:
        mediaEntries.size() == 1

        MediaEntry mediaEntry = mediaEntries['test-reference-id'][0]
        !mediaEntry.id.isEmpty()
        mediaEntry.referenceId == 'test-reference-id'
        def hlsStreamUrl = mediaEntry.streams.withFormat(StreamFormat.APPLE_HDS)
        hlsStreamUrl != null
        hlsStreamUrl.contains('/entryId/' + mediaEntry.id)
        hlsStreamUrl.contains('/format/applehttp')
        mediaEntry.duration != null
        mediaEntry.thumbnailUrl.contains('/entry_id/' + mediaEntry.id)
        mediaEntry.thumbnailUrl.contains('/width/{thumbnailWidth}')
        mediaEntry.videoPreviewUrl.contains('/entry_id/' + mediaEntry.id)
        mediaEntry.videoPreviewUrl.contains('/width/{thumbnailWidth}')
        mediaEntry.videoPreviewUrl.contains('/vid_slices/{thumbnailCount}')
        mediaEntry.downloadUrl.contains('/entryId/' + mediaEntry.id)
        mediaEntry.downloadUrl.contains('/format/download')
        mediaEntry.getStatus() == MediaEntryStatus.NOT_READY

        where:
        client << [realClient(), testClient()]
    }

    def "create and list caption files"(KalturaClient client) {
        given:
        client.createMediaEntry("test-reference-id")
        client.createMediaEntry("another-test-reference-id")

        when:
        CaptionAsset captionAsset = CaptionAsset.builder()
                .label("English (auto-generated)")
                .language(KalturaLanguage.ENGLISH)
                .fileType(CaptionFormat.WEBVTT)
                .build()
        client.createCaptionsFile("test-reference-id", captionAsset, readResourceFile("/captions.vtt"))
        List<CaptionAsset> emptyCaptions = client.getCaptionFilesByReferenceId("another-test-reference-id")
        List<CaptionAsset> captions = client.getCaptionFilesByReferenceId("test-reference-id")
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

    def "delete caption files"(KalturaClient client) {
        given:
        client.createMediaEntry("test-reference-id")
        CaptionAsset captionAsset = CaptionAsset.builder()
                .label("English (auto-generated)")
                .language(KalturaLanguage.ENGLISH)
                .fileType(CaptionFormat.WEBVTT)
                .build()
        String assetId = client.createCaptionsFile("test-reference-id", captionAsset, readResourceFile("/captions.vtt")).id

        when:
        client.deleteCaptionContentByAssetId(assetId)
        List<CaptionAsset> assets = client.getCaptionFilesByReferenceId("test-reference-id")

        then:
        assets.isEmpty()

        where:
        client << [realClient(), testClient()]
    }

    def "can tag base entries"() {
        given:
        client.createMediaEntry("test-reference-id")
        Map<String, List<MediaEntry>> mediaEntries = client.getMediaEntriesByReferenceIds([
                "test-reference-id",
        ])
        String entryId = mediaEntries.get("test-reference-id")[0].id

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
