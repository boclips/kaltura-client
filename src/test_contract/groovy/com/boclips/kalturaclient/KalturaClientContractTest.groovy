package com.boclips.kalturaclient

import com.boclips.kalturaclient.captionasset.CaptionAsset
import com.boclips.kalturaclient.captionasset.CaptionFormat
import com.boclips.kalturaclient.captionasset.KalturaLanguage
import com.boclips.kalturaclient.flavorParams.FlavorParams
import com.boclips.kalturaclient.flavorParams.Quality
import com.boclips.kalturaclient.media.MediaEntry
import com.boclips.kalturaclient.media.MediaEntryStatus
import com.boclips.kalturaclient.testsupport.TestFactories
import org.apache.commons.io.IOUtils
import org.yaml.snakeyaml.Yaml
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.time.ZonedDateTime
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
            realClient().deleteEntriesByReferenceId(referenceId)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    def "retrieve assets by entry id"(KalturaClient client) {
        given:
        def entryId = "1_zk9l1gj8"

        when:
        def assets = client.getAssetsByEntryId(entryId)

        then:
        assets.size() == 1
        assets.first().id == "1_eian2fxp"
        assets.first().entryId == "1_zk9l1gj8"
        assets.first().bitrateKbps == 377
        assets.first().sizeKb == 6645
        assets.first().width == 320
        assets.first().height == 176
        assets.first().createdAt == ZonedDateTime.parse("2019-11-11T18:03:33Z")

        where:
        client << [realClient(), testClient()]
    }

    def "retrieve assets by entry ids"(KalturaClient client) {
        given:
        def entryIds = ["1_zk9l1gj8", "1_1sv8y1q6"]

        when:
        def retrievedAssetsByEntryIds = client.getAssetsByEntryIds(entryIds)

        then:
        retrievedAssetsByEntryIds.keySet().size() == 2
        retrievedAssetsByEntryIds.get("1_zk9l1gj8").size() == 1
        retrievedAssetsByEntryIds.get("1_zk9l1gj8").first().id == "1_eian2fxp"

        where:
        client << [realClient(), testClient()]
    }

    def "retrieve assets by entry ids when no entry ids"(KalturaClient client) {
        given:
        def entryIds = []

        when:
        def retrievedAssetsByEntryIds = client.getAssetsByEntryIds(entryIds)

        then:
        retrievedAssetsByEntryIds.size() == 0

        where:
        client << [realClient(), testClient()]
    }

    def "create and delete media entries"(KalturaClient client) {
        given:
        def referenceId = UUID.randomUUID().toString()
        client.createEntry(referenceId)
        List<MediaEntry> createdMediaEntry = client.getEntriesByReferenceId(referenceId)

        when:
        client.deleteEntriesByReferenceId(referenceId)
        List<MediaEntry> deletedMediaEntry = client.getEntriesByReferenceId(referenceId)

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
        client.createEntry(referenceId)
        List<MediaEntry> createdMediaEntries = client.getEntriesByReferenceId(referenceId)

        when:
        client.deleteEntryById(createdMediaEntries.get(0).id)
        List<MediaEntry> deletedMediaEntry = client.getEntriesByReferenceId(referenceId)

        then:
        createdMediaEntries.size() == 1
        createdMediaEntries[0].referenceId == referenceId
        deletedMediaEntry.isEmpty()

        where:
        client << [realClient(), testClient()]
    }

    def "fetch media entries from api by reference id"(KalturaClient client) {
        given:
        client.createEntry(referenceIdOne)

        when:
        Map<String, List<MediaEntry>> mediaEntries = client.getEntriesByReferenceIds([
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
        client.createEntry(referenceIdOne)

        when:
        Map<String, List<MediaEntry>> mediaEntries = client.getEntriesByReferenceIds([
                referenceIdOne
        ])

        then:
        mediaEntries.size() == 1
        mediaEntries[referenceIdOne].size().equals(1)
        MediaEntry mediaEntry = mediaEntries[referenceIdOne][0]

        MediaEntry mediaEntryById = client.getEntriesByIds([mediaEntry.id]).get(mediaEntry.id)

        mediaEntry.id.equals(mediaEntryById.id)
        mediaEntry.referenceId.equals(mediaEntryById.referenceId)

        where:
        client << [realClient(), testClient()]
    }

    def "fetch media entries from api by entry id"(KalturaClient client) {
        given:
        client.createEntry(referenceIdOne)

        when:
        Map<String, List<MediaEntry>> mediaEntries = client.getEntriesByReferenceIds([
                referenceIdOne
        ])

        then:
        mediaEntries.size() == 1
        mediaEntries[referenceIdOne].size().equals(1)
        MediaEntry mediaEntry = mediaEntries[referenceIdOne][0]

        MediaEntry mediaEntryById = client.getEntryById(mediaEntry.id)

        mediaEntry.id.equals(mediaEntryById.id)
        mediaEntry.referenceId.equals(mediaEntryById.referenceId)

        where:
        client << [realClient(), testClient()]
    }

    def "create and list caption files by reference id"(KalturaClient client) {
        given:
        client.createEntry(referenceIdOne)
        client.createEntry(referenceIdTwo)

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
        client.createEntry(referenceIdOne)
        MediaEntry mediaEntry = client.getEntriesByReferenceId(referenceIdOne).get(0)

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
        client.createEntry(referenceIdOne)
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
        client.createEntry(referenceIdOne)
        Map<String, List<MediaEntry>> mediaEntries = client.getEntriesByReferenceIds([
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

    def "gets flavors"() {
        when:
        List<FlavorParams> flavorParams = client.getFlavorParams()

        Map<Integer, List<FlavorParams>> flavorParamsMap = flavorParams.groupBy { it.id }

        then:
        flavorParamsMap.size() > 1

        flavorParamsMap.get(487041).first().getHeight() == 360
        flavorParamsMap.get(487041).first().getWidth() == 0
        flavorParamsMap.get(487041).first().getQuality() == Quality.LOW

        flavorParamsMap.get(487071).first().getHeight() == 720
        flavorParamsMap.get(487071).first().getWidth() == 0
        flavorParamsMap.get(487071).first().getQuality() == Quality.MEDIUM

        flavorParamsMap.get(487081).first().getHeight() == 720
        flavorParamsMap.get(487081).first().getWidth() == 0
        flavorParamsMap.get(487081).first().getQuality() == Quality.HIGH

        flavorParamsMap.get(487091).first().getHeight() == 1080
        flavorParamsMap.get(487091).first().getWidth() == 0
        flavorParamsMap.get(487091).first().getQuality() == Quality.HIGH

        where:
        client << [testClient(), realClient()]
    }

//    @Ignore("This is pretty expensive, in terms of time. Run at your own risk.")
    def "fetch all videos"(KalturaClient client) {
        given:
        client.createEntry(referenceIdOne)

        when:
        Iterator<MediaEntry> mediaEntriesIterator = client.getEntries()

        then:

        def index = mediaEntriesIterator.findIndexOf { mediaEntry ->
            mediaEntry.referenceId == referenceIdOne
        }

        index != -1

        where:
        client << [testClient(), realClient()]
    }

    private static TestKalturaClient testClient() {
        def testClient = new TestKalturaClient()
        testClient.setAssets("1_zk9l1gj8", Collections.singletonList(TestFactories.asset("1_eian2fxp", 6645, 377, 0, "1_zk9l1gj8", false, 320, 176, ZonedDateTime.parse("2019-11-11T18:03:33Z"))))
        testClient.setAssets("1_1sv8y1q6", Collections.singletonList(TestFactories.asset("1_ogi1ui0u")))
        return testClient
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
