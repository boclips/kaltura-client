package com.boclips.kalturaclient

import com.boclips.kalturaclient.streams.StreamFormat
import com.boclips.kalturaclient.streams.StreamUrls
import spock.lang.Specification

import java.time.Duration

class KalturaClientE2E extends Specification {

    def "fetch media entries from api"(client) {
        when:
        Map<String, MediaEntry> mediaEntries = client.mediaEntriesByReferenceIds(
                "97eea646-c35b-4921-991d-95352666bd3a",
                "750af1ea-cbeb-4047-8d48-7ef067bfedfb",
                "unknown-reference-id")

        then:
        mediaEntries.size() == 2
        mediaEntries['97eea646-c35b-4921-991d-95352666bd3a'].id == '1_2t65w8sx'
        mediaEntries['97eea646-c35b-4921-991d-95352666bd3a'].referenceId == '97eea646-c35b-4921-991d-95352666bd3a'
        mediaEntries['97eea646-c35b-4921-991d-95352666bd3a'].streams.withFormat(StreamFormat.APPLE_HDS) != null
        mediaEntries['97eea646-c35b-4921-991d-95352666bd3a'].duration == Duration.ofMinutes(1).plusSeconds(32)
        mediaEntries['97eea646-c35b-4921-991d-95352666bd3a'].thumbnailUrl == 'https://cfvod.kaltura.com/p/2394162/sp/239416200/thumbnail/entry_id/1_2t65w8sx/version/100011'

        mediaEntries['750af1ea-cbeb-4047-8d48-7ef067bfedfb'].id == '1_8atxygq9'

        where:
        client << [realClient(), testClient()]
    }

    def testClient() {
        def client = new TestKalturaClient()
        client.addMediaEntry(mediaEntry("1_2t65w8sx", "97eea646-c35b-4921-991d-95352666bd3a", Duration.ofSeconds(92)))
        client.addMediaEntry(mediaEntry("1_8atxygq9", "750af1ea-cbeb-4047-8d48-7ef067bfedfb", Duration.ofSeconds(185)))
        return client
    }

    def realClient() {
        KalturaClientConfig config = KalturaClientConfig.builder()
                .partnerId(System.getenv("PARTNER_ID"))
                .userId(System.getenv("USER_ID"))
                .secret(System.getenv("KALTURA_SECRET"))
                .build()

        return KalturaClient.create(config)
    }

    private static MediaEntry mediaEntry(String id, String referenceId, Duration duration) {
        MediaEntry.builder()
                .id(id)
                .referenceId(referenceId)
                .duration(duration)
                .streams(new StreamUrls("https://stream.com/s/"+id+"[FORMAT]"))
                .thumbnailUrl("https://cfvod.kaltura.com/p/2394162/sp/239416200/thumbnail/entry_id/"+id+"/version/100011")
                .build()
    }
}
