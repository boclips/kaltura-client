package com.boclips.kalturaclient

import com.boclips.kalturaclient.config.KalturaClientConfig
import com.boclips.kalturaclient.config.KalturaClientConfigException
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
                .build()

        then:
        config.partnerId == "partner-id"
        config.userId == "user-id"
        config.secret == "secret"
        config.baseUrl == "common://kaltura.com/api"
        config.sessionTtl == 120
    }

    def 'throws when userId is blank'() {
        when:
        KalturaClientConfig.builder()
                .partnerId("partner id")
                .userId("")
                .secret("secret")
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
                .build()

        then:
        KalturaClientConfigException ex = thrown()
        ex.message == "Invalid secret: []"
    }
}
