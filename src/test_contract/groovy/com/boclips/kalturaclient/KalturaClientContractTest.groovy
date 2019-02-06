package com.boclips.kalturaclient

import com.boclips.kalturaclient.media.MediaEntry
import com.boclips.kalturaclient.media.MediaEntryStatus
import com.boclips.kalturaclient.media.resources.MediaEntryStatusResource
import com.boclips.kalturaclient.media.streams.StreamFormat
import com.boclips.kalturaclient.media.streams.StreamUrls
import org.yaml.snakeyaml.Yaml
import spock.lang.Specification

import java.time.Duration

class KalturaClientContractTest extends Specification {

    void setup() {
        realClient().createMediaEntry("test-reference-id")
    }

    void cleanup() {
        realClient().deleteMediaEntriesByReferenceId("test-reference-id")
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
        mediaEntry.getStatus() == MediaEntryStatus.NOT_READY

        where:
        client << [realClient(), testClient()]
    }

    private KalturaClient testClient() {
        def client = new TestKalturaClient()
        client.addMediaEntry(mediaEntry("1_2t65w8sx", "test-reference-id", Duration.ofSeconds(92), MediaEntryStatus.NOT_READY))
        client.addMediaEntry(mediaEntry("1_8atxygq9", "reference-id-2", Duration.ofSeconds(185), MediaEntryStatus.NOT_READY))
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

    private static MediaEntry mediaEntry(String id, String referenceId, Duration duration, MediaEntryStatus status) {
        MediaEntry.builder()
                .id(id)
                .referenceId(referenceId)
                .duration(duration)
                .streams(new StreamUrls("https://stream.com/s/" + id + "[FORMAT]"))
                .thumbnailUrl("https://cdnapisec.kaltura.com/p/2394162/thumbnail/entry_id/" + id + "/height/250/vid_slices/3/vid_slice/2")
                .status(status)
                .build()
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
