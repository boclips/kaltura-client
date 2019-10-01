package com.boclips.kalturaclient

import spock.lang.Specification

class KalturaClientConfigTest extends Specification {

    def 'builder creates a config'() {
        when:
        KalturaClientConfig config = KalturaClientConfig.builder()
                .partnerId("partner-id")
                .userId("user-id")
                .secret("secret")
                .baseUrl("common://kaltura.com/api")
                .sessionTtl(120)
                .streamFlavorParamIds("1,2,3")
                .build()

        then:
        config.partnerId == "partner-id"
        config.userId == "user-id"
        config.secret == "secret"
        config.baseUrl == "common://kaltura.com/api"
        config.sessionTtl == 120
        config.streamFlavorParamIds == "1,2,3"
    }

    def 'accepts a single streamFlavorParamId'() {
        when:
        KalturaClientConfig config = KalturaClientConfig.builder()
                .partnerId("partner-id")
                .userId("user-id")
                .secret("secret")
                .baseUrl("common://kaltura.com/api")
                .sessionTtl(120)
                .streamFlavorParamIds("13")
                .build()

        then:
        config.streamFlavorParamIds == "13"
    }

    def 'throws when userId is blank'() {
        when:
        KalturaClientConfig.builder()
                .partnerId("partner id")
                .userId("")
                .secret("secret")
                .streamFlavorParamIds("1,2,3")
                .build()

        then:
        KalturaClientConfigException ex = thrown()
        ex.message == "Invalid user id: []"
    }

    def 'throws when partnerId is blank'() {
        when:
        KalturaClientConfig.builder()
                .partnerId("")
                .userId("user")
                .secret("secret")
                .streamFlavorParamIds("1,2,3")
                .build()

        then:
        KalturaClientConfigException ex = thrown()
        ex.message == "Invalid partner id: []"
    }

    def 'throws when secret is blank'() {
        when:
        KalturaClientConfig.builder()
                .partnerId("partnerid")
                .userId("user")
                .secret("")
                .streamFlavorParamIds("1,2,3")
                .build()

        then:
        KalturaClientConfigException ex = thrown()
        ex.message == "Invalid secret: []"
    }

    def 'throws when streamFlavorParamIds is blank'() {
        when:
        KalturaClientConfig.builder()
                .partnerId("partnerid")
                .userId("user")
                .secret("secret")
                .streamFlavorParamIds("")
                .build()

        then:
        KalturaClientConfigException ex = thrown()
        ex.message == "Invalid streamFlavorParamIds: []"
    }

    def 'throws when streamFlavorParamIds is not a number or list of numbers'() {
        when:
        KalturaClientConfig.builder()
                .partnerId("partnerid")
                .userId("user")
                .secret("secret")
                .streamFlavorParamIds("af32")
                .build()

        then:
        KalturaClientConfigException ex = thrown()
        ex.message == "Invalid streamFlavorParamIds: [af32]. Must be a comma separated list of numbers"
    }

}
