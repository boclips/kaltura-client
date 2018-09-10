package com.boclips.kalturaclient

import com.boclips.kalturaclient.session.SessionGenerator
import spock.lang.Specification

class KalturaClientTest extends Specification {

    def "accepts a valid configuration"() {
        when:
        KalturaClientConfig config = KalturaClientConfig.builder()
                .baseUrl("http://www.kaltura.com")
                .userId("1")
                .secret("123")
                .partnerId("999")
                .build()

        then:
        new KalturaClient(config, Mock(SessionGenerator))
    }

    def "throws when userId not set"() {
        when:
        KalturaClientConfig config = KalturaClientConfig.builder()
                .baseUrl("http://www.kaltura.com")
                .secret("123")
                .partnerId("999")
                .build()
        new KalturaClient(config, Mock(SessionGenerator))

        then:
        thrown Exception
    }

    def "throws when secret not set"() {
        when:
        KalturaClientConfig config = KalturaClientConfig.builder()
                .baseUrl("http://www.kaltura.com")
                .userId("1")
                .partnerId("999")
                .build()
        new KalturaClient(config, Mock(SessionGenerator))

        then:
        thrown Exception
    }

    def "throws when partnerId not set"() {
        when:
        KalturaClientConfig config = KalturaClientConfig.builder()
                .baseUrl("http://www.kaltura.com")
                .userId("1")
                .secret("123")
                .build()
        new KalturaClient(config, Mock(SessionGenerator))

        then:
        thrown Exception
    }

}
