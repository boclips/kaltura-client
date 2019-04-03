package com.boclips.kalturaclient

import com.boclips.kalturaclient.captionasset.CaptionAsset
import com.boclips.kalturaclient.captionasset.CaptionFormat
import com.boclips.kalturaclient.media.MediaEntry
import com.boclips.kalturaclient.media.MediaEntryStatus
import com.boclips.kalturaclient.media.streams.StreamFormat
import com.boclips.kalturaclient.media.streams.StreamUrls
import org.yaml.snakeyaml.Yaml
import spock.lang.Specification

import java.time.Duration
import java.util.stream.Collectors

class KalturaClientContractTest extends Specification {

    void setup() {
        cleanup()
        realClient().createMediaEntry("test-reference-id")
    }

    void cleanup() {
        try {
            realClient().deleteMediaEntriesByReferenceId("test-reference-id")
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
        mediaEntry.streams.withFormat(StreamFormat.APPLE_HDS) != null
        mediaEntry.duration != null
        mediaEntry.thumbnailUrl.startsWith('https://cdnapisec.kaltura.com/p')
        mediaEntry.downloadUrl.startsWith('https://cdnapisec.kaltura.com/p')
        mediaEntry.getStatus() == MediaEntryStatus.NOT_READY

        where:
        client << [realClient(), testClient()]
    }

    def "create and list caption files"(KalturaClient client) {
        when:
        CaptionAsset captionAsset = CaptionAsset.builder()
                .label("English (auto-generated)")
                .language("English")
                .fileType(CaptionFormat.WEBVTT)
                .build()
        client.createCaptionsFile("test-reference-id", captionAsset, "this week in the news")
        List<CaptionAsset> captions = client.getCaptionFilesByReferenceId("test-reference-id")
        List<String> contents = captions.stream()
                .map { caption -> caption.id }
                .map { id -> client.getCaptionContentByAssetId(id) }
                .collect(Collectors.toList())

        then:
        captions.size() == 1
        captions.first().id.length() > 0
        captions.first().label == "English (auto-generated)"
        captions.first().language == "English"
        captions.first().fileType == CaptionFormat.WEBVTT

        then:
        contents.size() == 1
        contents.first() == "this week in the news"

        where:
        client << [realClient(), testClient()]
    }

    private KalturaClient testClient() {
        def client = new TestKalturaClient()
        def id = "1_2t65w8sx"
        client.addMediaEntry(MediaEntry.builder()
                .id(id)
                .referenceId("test-reference-id")
                .downloadUrl(downloadUrl(id))
                .duration(Duration.ofSeconds(92))
                .streams(streamUrl(id))
                .thumbnailUrl(thumbnailUrl(id))
                .status(MediaEntryStatus.NOT_READY)
                .build())
        return client
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

    private static String downloadUrl(String id) {
        return "https://cdnapisec.kaltura.com/p/" + id + ".mp4"
    }

    private static StreamUrls streamUrl(String id) {
        new StreamUrls("https://stream.com/s/" + id + "[FORMAT]")
    }

    private static String thumbnailUrl(String id) {
        "https://cdnapisec.kaltura.com/p/2394162/thumbnail/entry_id/" + id + "/height/250/vid_slices/3/vid_slice/2"
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
}
