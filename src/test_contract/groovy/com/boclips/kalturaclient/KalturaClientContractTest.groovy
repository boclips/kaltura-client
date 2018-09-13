package com.boclips.kalturaclient

import com.boclips.kalturaclient.client.TestKalturaClient
import com.boclips.kalturaclient.streams.StreamFormat
import com.boclips.kalturaclient.streams.StreamUrls
import org.yaml.snakeyaml.Yaml
import spock.lang.Specification

import java.time.Duration

class KalturaClientContractTest extends Specification {
    def "fetch media entries from api"(client) {
        when:
        Map<String, MediaEntry> mediaEntries = client.mediaEntriesByReferenceIds(
                "97eea646-c35b-4921-991d-95352666bd3a",
                "750af1ea-cbeb-4047-8d48-7ef067bfedfb",
                "unknown-reference-id")

        then:
        mediaEntries.size() == 2

        MediaEntry mediaEntry = mediaEntries['97eea646-c35b-4921-991d-95352666bd3a']
        mediaEntry.id == '1_2t65w8sx'
        mediaEntry.referenceId == '97eea646-c35b-4921-991d-95352666bd3a'
        mediaEntry.streams.withFormat(StreamFormat.APPLE_HDS) != null
        mediaEntry.duration == Duration.ofMinutes(1).plusSeconds(32)
        mediaEntry.thumbnailUrl == 'https://cfvod.kaltura.com/p/2394162/sp/239416200/thumbnail/entry_id/1_2t65w8sx/version/100011'

        mediaEntries['750af1ea-cbeb-4047-8d48-7ef067bfedfb'].id == '1_8atxygq9'

        where:
        client << [realClient(), testClient()]
    }

    def "fetch existing media entry from api"(client) {
        when:
        MediaEntry mediaEntry = client.mediaEntryByReferenceId("97eea646-c35b-4921-991d-95352666bd3a").get()

        then:
        mediaEntry.id == '1_2t65w8sx'
        mediaEntry.referenceId == '97eea646-c35b-4921-991d-95352666bd3a'
        mediaEntry.streams.withFormat(StreamFormat.APPLE_HDS) != null
        mediaEntry.duration == Duration.ofMinutes(1).plusSeconds(32)
        mediaEntry.thumbnailUrl == 'https://cfvod.kaltura.com/p/2394162/sp/239416200/thumbnail/entry_id/1_2t65w8sx/version/100011'

        where:
        client << [realClient(), testClient()]
    }

    def "fetch non-existing media entry from api"(client) {
        when:
        Optional<MediaEntry> mediaEntry = client.mediaEntryByReferenceId("unknown-reference-id")

        then:
        !mediaEntry.isPresent()

        where:
        client << [realClient(), testClient()]
    }

    private KalturaClient testClient() {
        def client = new TestKalturaClient()
        client.addMediaEntry(mediaEntry("1_2t65w8sx", "97eea646-c35b-4921-991d-95352666bd3a", Duration.ofSeconds(92)))
        client.addMediaEntry(mediaEntry("1_8atxygq9", "750af1ea-cbeb-4047-8d48-7ef067bfedfb", Duration.ofSeconds(185)))
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

    private static MediaEntry mediaEntry(String id, String referenceId, Duration duration) {
        MediaEntry.builder()
                .id(id)
                .referenceId(referenceId)
                .duration(duration)
                .streams(new StreamUrls("https://stream.com/s/" + id + "[FORMAT]"))
                .thumbnailUrl("https://cfvod.kaltura.com/p/2394162/sp/239416200/thumbnail/entry_id/" + id + "/version/100011")
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
