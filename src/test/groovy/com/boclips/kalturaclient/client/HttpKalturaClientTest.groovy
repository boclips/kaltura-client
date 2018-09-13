package com.boclips.kalturaclient.client

import com.boclips.kalturaclient.KalturaClientConfig
import com.boclips.kalturaclient.session.SessionGenerator
import spock.lang.Specification

class HttpKalturaClientTest extends Specification {

    def "accepts a valid configuration"() {
        when:
        KalturaClientConfig config = KalturaClientConfig.builder()
                .baseUrl("http://www.kaltura.com")
                .userId("1")
                .secret("123")
                .partnerId("999")
                .build()

        then:
        new HttpKalturaClient(config, Mock(SessionGenerator))
    }

    def "throws when userId not set"() {
        when:
        KalturaClientConfig config = KalturaClientConfig.builder()
                .baseUrl("http://www.kaltura.com")
                .secret("123")
                .partnerId("999")
                .build()
        new HttpKalturaClient(config, Mock(SessionGenerator))

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
        new HttpKalturaClient(config, Mock(SessionGenerator))

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
        new HttpKalturaClient(config, Mock(SessionGenerator))

        then:
        thrown Exception
    }

}
