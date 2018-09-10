package com.boclips.kalturaclient

import spock.lang.Specification

class KalturaClientTest extends Specification {

    def "accepts a valid configuration"() {
        expect:
        new KalturaClient(KalturaClientConfig.builder()
                .baseUrl("http://www.kaltura.com")
                .userId("1")
                .secret("123")
                .partnerId("999")
                .build())
    }

    def "throws when baseUrl not set"() {
        when:
        new KalturaClient(KalturaClientConfig.builder()
                .userId("1")
                .secret("123")
                .partnerId("999")
                .build())

        then:
        thrown Exception
    }

    def "throws when userId not set"() {
        when:
        new KalturaClient(KalturaClientConfig.builder()
                .baseUrl("http://www.kaltura.com")
                .secret("123")
                .partnerId("999")
                .build())

        then:
        thrown Exception
    }

    def "throws when secret not set"() {
        when:
        new KalturaClient(KalturaClientConfig.builder()
                .baseUrl("http://www.kaltura.com")
                .userId("1")
                .partnerId("999")
                .build())

        then:
        thrown Exception
    }

    def "throws when partnerId not set"() {
        when:
        new KalturaClient(KalturaClientConfig.builder()
                .baseUrl("http://www.kaltura.com")
                .userId("1")
                .secret("123")
                .build())

        then:
        thrown Exception
    }

}
