package com.boclips.kalturaclient

import com.boclips.kalturaclient.captionasset.CaptionAsset
import com.boclips.kalturaclient.captionasset.CaptionFormat
import com.boclips.kalturaclient.captionasset.KalturaLanguage
import com.boclips.kalturaclient.clients.TestKalturaClient
import com.boclips.kalturaclient.config.KalturaClientConfig
import com.boclips.kalturaclient.flavorParams.FlavorParams
import com.boclips.kalturaclient.flavorParams.Quality
import com.boclips.kalturaclient.media.MediaEntry
import com.boclips.kalturaclient.testsupport.TestFactories
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.StringUtils
import org.yaml.snakeyaml.Yaml
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.time.ZonedDateTime
import java.util.stream.Collectors

class KalturaClientContractTest extends Specification {
    String referenceId = UUID.randomUUID().toString()
    MediaEntry createdMediaEntry = null


    void cleanup() {
        if (createdMediaEntry != null) {
            this.tryDeleteMediaEntry(createdMediaEntry.id)
        }

        createdMediaEntry = null
    }

    void tryDeleteMediaEntry(String id) {
        try {
            realClient().deleteEntry(id)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    MediaEntry create(KalturaClient client, String referenceId) {
        createdMediaEntry = client.createEntry(referenceId)
        return createdMediaEntry
    }

    def "retrieve assets by entry id"(KalturaClient client) {
        given:
        def entryId = "1_zk9l1gj8"

        when:
        def assets = client.getVideoAssets(entryId)

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
        def retrievedAssetsByEntryIds = client.getVideoAssets(entryIds)

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
        def retrievedAssetsByEntryIds = client.getEntries(entryIds)

        then:
        retrievedAssetsByEntryIds.size() == 0

        where:
        client << [realClient(), testClient()]
    }

    def "delete media entries by entryId"(KalturaClient client) {
        given:
        def referenceId = UUID.randomUUID().toString()
        MediaEntry mediaEntry = client.createEntry(referenceId)

        when:
        client.deleteEntry(mediaEntry.id)
        Map<String, MediaEntry> deletedMediaEntry = client.getEntries([mediaEntry.id])

        then:
        mediaEntry.referenceId == referenceId
        deletedMediaEntry.isEmpty()

        where:
        client << [realClient(), testClient()]
    }

    def "fetch media entries from api by entry id"(KalturaClient client) {
        given:
        MediaEntry mediaEntry = create(client, referenceId)

        when:
        MediaEntry mediaEntryById = client.getEntry(mediaEntry.id)

        then:
        mediaEntry.id == mediaEntryById.id
        mediaEntry.referenceId == mediaEntryById.referenceId

        where:
        client << [realClient(), testClient()]
    }


    def "fetch download caption asset url by caption id"(KalturaClient client) {
        given:
        MediaEntry mediaEntry = create(client, referenceId)

        when:
        CaptionAsset captionAsset = CaptionAsset.builder()
                .label("English (auto-generated)")
                .language(KalturaLanguage.ENGLISH)
                .fileType(CaptionFormat.WEBVTT)
                .build()
        client.createCaptionForVideo(mediaEntry.id, captionAsset, readResourceFile("/captions.vtt"))

        List<CaptionAsset> captions = client.getCaptionsForVideo(mediaEntry.id)

        String captionId = captions.get(0).getId()
        URI captionAssetUri = client.getCaptionAssetUrl(captionId)

        then:
        captionAssetUri != null
        String url = captionAssetUri.toString()
        StringUtils.isNotBlank(url)
        url.contains(captionId)
        url.startsWith("http://") || url.startsWith("https://")

        where:
        client << [realClient(), testClient()]
    }

    def "create and list caption files by entry id"(KalturaClient client) {
        given:
        MediaEntry mediaEntry = create(client, referenceId)

        when:
        CaptionAsset captionAsset = CaptionAsset.builder()
                .label("English (auto-generated)")
                .language(KalturaLanguage.ENGLISH)
                .fileType(CaptionFormat.WEBVTT)
                .build()
        client.createCaptionForVideo(mediaEntry.id, captionAsset, readResourceFile("/captions.vtt"))
        List<CaptionAsset> captions = client.getCaptionsForVideo(mediaEntry.id)
        List<String> contents = captions.stream()
                .map { caption -> caption.id }
                .map { id -> client.getCaptionContent(id) }
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
        MediaEntry entry = client.createEntry(referenceId)
        CaptionAsset captionAsset = CaptionAsset.builder()
                .label("English (auto-generated)")
                .language(KalturaLanguage.ENGLISH)
                .fileType(CaptionFormat.WEBVTT)
                .build()
        String assetId = client.createCaptionForVideo(entry.id, captionAsset, readResourceFile("/captions.vtt")).id

        when:
        client.deleteCaption(assetId)
        List<CaptionAsset> assets = client.getCaptionsForVideo(entry.id)

        then:
        assets.isEmpty()

        where:
        client << [realClient(), testClient()]
    }

    def "can tag base entries"() {
        given:
        MediaEntry mediaEntry = create(client, referenceId)

        String entryId = mediaEntry.id

        when:
        client.tag(entryId, ["just", "testing"])

        then:
        client.getBaseEntry(entryId).tags == ["just", "testing"]

        where:
        client << [testClient(), realClient()]
    }

    def "can request captions"() {
        given:
        MediaEntry mediaEntry = create(client, referenceId)

        String entryId = mediaEntry.id

        when:
        client.requestCaption(entryId)

        then:
        client.getBaseEntry(entryId).tags == ["caption48"]

        where:
        client << [testClient(), realClient()]
    }

    def "fetch human generated captions if available"(KalturaClient client) {
        given:
        MediaEntry mediaEntry = create(client, referenceId)

        when:
        def autoGeneratedCaptions = CaptionAsset.builder()
                .id("id1")
                .label("English (auto-generated)")
                .language(KalturaLanguage.ENGLISH)
                .fileType(CaptionFormat.WEBVTT)
                .build()
        client.createCaptionForVideo(mediaEntry.id, autoGeneratedCaptions, readResourceFile("/captions.vtt"))

        def humanGeneratedCaptions = CaptionAsset.builder()
                .id("id2")
                .label("English")
                .language(KalturaLanguage.ENGLISH)
                .fileType(CaptionFormat.WEBVTT)
                .build()
        client.createCaptionForVideo(mediaEntry.id, humanGeneratedCaptions, readResourceFile("/captions.vtt"))

        def captionAsset = client.getHumanGeneratedCaptionAsset(mediaEntry.id)

        then:
        captionAsset != null
        captionAsset.label == "English"

        where:
        client << [realClient(), testClient()]
    }

    def "does not serve auto-generated caption when requesting human generates ones"(KalturaClient client) {
        given:
        MediaEntry mediaEntry = create(client, referenceId)

        when:
        def autoGeneratedCaptions = CaptionAsset.builder()
                .id("id1")
                .label("English (auto-generated)")
                .language(KalturaLanguage.ENGLISH)
                .fileType(CaptionFormat.WEBVTT)
                .build()
        client.createCaptionForVideo(mediaEntry.id, autoGeneratedCaptions, readResourceFile("/captions.vtt"))

        def captionAsset = client.getHumanGeneratedCaptionAsset(mediaEntry.id)

        then:
        captionAsset == null

        where:
        client << [realClient(), testClient()]
    }

    def "fetch caption status by entry id - available captions"(KalturaClient client) {
        given:
        MediaEntry mediaEntry = create(client, referenceId)

        when:
        def captionAsset = CaptionAsset.builder()
                .label("English (auto-generated)")
                .language(KalturaLanguage.ENGLISH)
                .fileType(CaptionFormat.WEBVTT)
                .build()
        client.createCaptionForVideo(mediaEntry.id, captionAsset, readResourceFile("/captions.vtt"))
        def captionStatus = client.getCaptionStatus(mediaEntry.id)

        then:
        captionStatus == KalturaCaptionManager.CaptionStatus.AUTO_GENERATED_AVAILABLE

        where:
        client << [realClient(), testClient()]
    }

    def "fetch caption status by entry id with multiple captions - available captions"(KalturaClient client) {
        given:
        MediaEntry mediaEntry = create(client, referenceId)

        when:
        def captionAsset = CaptionAsset.builder()
                .label("English (auto-generated)")
                .language(KalturaLanguage.ENGLISH)
                .fileType(CaptionFormat.WEBVTT)
                .build()
        client.createCaptionForVideo(mediaEntry.id, captionAsset, readResourceFile("/captions.vtt"))

        def humanGeneratedCaptionAsset = CaptionAsset.builder()
                .label("English")
                .language(KalturaLanguage.ENGLISH)
                .fileType(CaptionFormat.SRT)
                .build()
        client.createCaptionForVideo(mediaEntry.id, humanGeneratedCaptionAsset, readResourceFile("/captions.vtt"))
        def captionStatus = client.getCaptionStatus(mediaEntry.id)

        then:
        captionStatus == KalturaCaptionManager.CaptionStatus.HUMAN_GENERATED_AVAILABLE

        where:
        client << [realClient(), testClient()]
    }

    def "fetch caption status by entry id - no captions requested, no captions available"(KalturaClient client) {
        given:
        MediaEntry mediaEntry = create(client, referenceId)

        when:
        def captionStatus = client.getCaptionStatus(mediaEntry.id)

        then:
        captionStatus == KalturaCaptionManager.CaptionStatus.NOT_AVAILABLE

        where:
        client << [realClient(), testClient()]
    }

    def "fetch caption status by entry id - captions requested"(KalturaClient client) {
        given:
        MediaEntry mediaEntry = create(client, referenceId)

        when:
        client.requestCaption(mediaEntry.id)
        def captionStatus = client.getCaptionStatus(mediaEntry.id)

        then:
        captionStatus == KalturaCaptionManager.CaptionStatus.REQUESTED

        where:
        client << [realClient(), testClient()]
    }


    def "fetch caption status by entry id with already auto generated catpion - captions requested"(KalturaClient client) {
        given:
        MediaEntry mediaEntry = create(client, referenceId)

        when:
        def captionAsset = CaptionAsset.builder()
                .label("English (auto-generated)")
                .language(KalturaLanguage.ENGLISH)
                .fileType(CaptionFormat.WEBVTT)
                .build()
        client.createCaptionForVideo(mediaEntry.id, captionAsset, readResourceFile("/captions.vtt"))
        client.requestCaption(mediaEntry.id)



        def captionStatus = client.getCaptionStatus(mediaEntry.id)

        then:
        captionStatus == KalturaCaptionManager.CaptionStatus.REQUESTED

        where:
        client << [realClient(), testClient()]
    }

    def "fetch caption status by entry id - captions being processed"(KalturaClient client) {
        given:
        MediaEntry mediaEntry = create(client, referenceId)

        when:
        client.tag(mediaEntry.id, Arrays.asList("processing"))
        def captionStatus = client.getCaptionStatus(mediaEntry.id)


        then:
        captionStatus == KalturaCaptionManager.CaptionStatus.PROCESSING

        where:
        client << [realClient(), testClient()]
    }

    def "upload thumbnail image to entry"(KalturaClient client) {
        given:
        MediaEntry mediaEntry = create(client, referenceId)

        when:
        InputStream fileStream = openResourceFile("/custom-thumbnail.jpeg")
        def thumbAssetId = client.addThumbnailFromImage(mediaEntry.id, fileStream, "custom-thumbnail.jpeg");

        then:
        thumbAssetId != null

        where:
        client << [realClient(), testClient()]
    }

    def "set custom thumbnail image as default thumbnail"(KalturaClient client) {
        given:
        MediaEntry mediaEntry = create(client, referenceId)
        //first image is implicitly default since there were no other thumbnails before
        client.addThumbnailFromImage(mediaEntry.id, openResourceFile("/custom-thumbnail.jpeg"), "custom-thumbnail.jpeg");
        def entryBefore = client.getBaseEntry(mediaEntry.id);
        def thumbAssetId = client.addThumbnailFromImage(mediaEntry.id, openResourceFile("/custom-thumbnail.jpeg"), "custom-thumbnail2.jpeg");

        when:
        client.setThumbnailAsDefault(thumbAssetId);
        def entryAfter = client.getBaseEntry(mediaEntry.id);

        then:
        entryAfter.thumbnailUrl != entryBefore.thumbnailUrl;

        where:
        client << [realClient(), testClient()]
    }

    def "fetch caption status by entry id - nonsensical tags"(KalturaClient client) {
        given:
        MediaEntry mediaEntry = create(client, referenceId)

        when:
        client.tag(mediaEntry.id, Arrays.asList("duknow"))
        def captionStatus = client.getCaptionStatus(mediaEntry.id)

        then:
        captionStatus == KalturaCaptionManager.CaptionStatus.UNKNOWN

        where:
        client << [realClient(), testClient()]
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

    def "gets flavor download URL"() {
        when:
        def assetId = "1_eian2fxp"
        URI downloadUrl = client.getDownloadAssetUrl(assetId)

        then:
        downloadUrl.toString().contains(assetId)

        where:
        client << [testClient(), realClient()]
    }

//    @Ignore("This is pretty expensive, in terms of time. Run at your own risk.")
    def "fetch all videos"(KalturaClient client) {
        given:
        create(client, referenceId)

        when:
        Iterator<MediaEntry> mediaEntriesIterator = client.getEntries()

        then:

        def index = mediaEntriesIterator.findIndexOf { mediaEntry ->
            mediaEntry.referenceId == referenceId
        }

        index != -1

        where:
        client << [testClient(), realClient()]
    }

    private static TestKalturaClient testClient() {
        def testClient = new TestKalturaClient()
        testClient.setAssets("1_zk9l1gj8", Collections.singletonList(TestFactories.asset("1_eian2fxp", 6645, 377, 0, "1_zk9l1gj8", false, 320, 176, ZonedDateTime.parse("2019-11-11T18:03:33Z"))))
        testClient.setAssets("1_1sv8y1q6", Collections.singletonList(TestFactories.asset("1_ogi1ui0u")))
        testClient.addMediaEntry(MediaEntry.builder().id("1_zk9l1gj8").build())
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
            is = openResourceFile(path)
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

    private InputStream openResourceFile(String path) {
        return KalturaClientContractTest.class.getResourceAsStream(path);
    }
}
