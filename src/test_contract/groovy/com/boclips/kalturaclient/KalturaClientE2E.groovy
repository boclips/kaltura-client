package com.boclips.kalturaclient


import spock.lang.Specification

class KalturaClientE2E extends Specification {

    def "fetch media entries from api"(client) {
        when:
        List<MediaEntry> mediaEntries = client.mediaEntriesByReferenceIds(
                "97eea646-c35b-4921-991d-95352666bd3a",
                "750af1ea-cbeb-4047-8d48-7ef067bfedfb")

        then:
        mediaEntries[0].id == '1_2t65w8sx'
        mediaEntries[1].id == '1_8atxygq9'

        where:
        client << [realClient(), testClient()]
    }

    def testClient() {
        def client = new TestKalturaClient()
        client.addMediaEntry(MediaEntry.builder().id("1_2t65w8sx").referenceId("97eea646-c35b-4921-991d-95352666bd3a").build())
        client.addMediaEntry(MediaEntry.builder().id("1_8atxygq9").referenceId("750af1ea-cbeb-4047-8d48-7ef067bfedfb").build())
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
}
