package com.boclips.kalturaclient

import spock.lang.Specification

class KalturaClientE2E extends Specification {

    def "fetch media entries from api"() {
        given:
        KalturaClientConfig config = KalturaClientConfig.builder()
                .partnerId("x")
                .userId("user@somewhere.com")
                .secret("some secret")
                .build()

        when:
        KalturaClient kalturaClient = KalturaClient.create(config)
        List<MediaEntry> mediaEntries = kalturaClient.mediaEntriesByReferenceIds("239")

        then:
        mediaEntries[0].id == 'x'
    }

}
