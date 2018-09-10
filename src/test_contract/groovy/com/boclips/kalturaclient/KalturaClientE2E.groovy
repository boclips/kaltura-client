package com.boclips.kalturaclient


import spock.lang.Specification

class KalturaClientE2E extends Specification {

    def "fetch media entries from api"() {
        given:
        KalturaClientConfig config = KalturaClientConfig.builder()
                .partnerId(System.getenv("PARTNER_ID"))
                .userId(System.getenv("USER_ID"))
                .secret(System.getenv("KALTURA_SECRET"))
                .build()

        when:
        KalturaClient kalturaClient = KalturaClient.create(config)
        List<MediaEntry> mediaEntries = kalturaClient.mediaEntriesByReferenceIds(
                "97eea646-c35b-4921-991d-95352666bd3a",
                "750af1ea-cbeb-4047-8d48-7ef067bfedfb")

        then:
        mediaEntries[0].id == '1_2t65w8sx'
        mediaEntries[1].id == '1_8atxygq9'
    }

}
