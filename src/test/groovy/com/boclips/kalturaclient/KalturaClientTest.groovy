package com.boclips.kalturaclient

import spock.lang.Specification

class KalturaClientTest extends Specification {

    def "can generate a Kaltura session"() {
        given:
        KalturaClient client = new KalturaClient()

        when:
        KalturaSession session = client.generateSession(100)

        then:
        session.toString() == 'x'
    }

}
