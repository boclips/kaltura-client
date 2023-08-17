package com.boclips.kalturaclient

import com.boclips.kalturaclient.config.KalturaClientConfig
import com.boclips.kalturaclient.config.KalturaClientConfigException
import spock.lang.Specification

class KalturaClientConfigTest extends Specification {

    def 'builder creates a config'() {
        when:
        KalturaClientConfig config = KalturaClientConfig.builder()
                .partnerId(123)
                .userId("user-id")
                .secret("secret")
                .baseUrl("common://kaltura.com/api")
                .sessionTtl(120)
                .streamingLinkSessionTtlHours(123)
                .captionProviderApiKey("apiKey")
                .captionProviderHostname("hostname.com")
                .build()

        then:
        config.partnerId == 123
        config.userId == "user-id"
        config.secret == "secret"
        config.baseUrl == "common://kaltura.com/api"
        config.sessionTtl == 120
        config.streamingLinkSessionTtlHours == 123
    }

    def 'throws when userId is blank'() {
        when:
        KalturaClientConfig.builder()
                .partnerId(123)
                .userId("")
                .secret("secret")
                .captionProviderApiKey("apiKey")
                .captionProviderHostname("hostname.com")
                .build()

        then:
        KalturaClientConfigException ex = thrown()
        ex.message == "Invalid user id: []"
    }

    def 'throws when partnerId is omitted'() {
        when:
        KalturaClientConfig.builder()
                .userId("user")
                .secret("secret")
                .captionProviderApiKey("apiKey")
                .captionProviderHostname("hostname.com")
                .build()

        then:
        KalturaClientConfigException ex = thrown()
        ex.message == "partner id not specified"
    }

    def 'throws when secret is blank'() {
        when:
        KalturaClientConfig.builder()
                .partnerId(123)
                .userId("user")
                .secret("")
                .captionProviderApiKey("apiKey")
                .captionProviderHostname("hostname.com")
                .build()

        then:
        KalturaClientConfigException ex = thrown()
        ex.message == "Invalid secret: []"
    }

    def 'throws when captionProviderApiKey is blank'() {
        when:
        KalturaClientConfig.builder()
                .partnerId(123)
                .userId("user")
                .secret("secret")
                .captionProviderApiKey("")
                .captionProviderHostname("hostname.com")
                .build()

        then:
        KalturaClientConfigException ex = thrown()
        ex.message == "Invalid captionProviderApiKey: []"
    }

    def 'throws when captionProviderHostname is blank'() {
        when:
        KalturaClientConfig.builder()
                .partnerId(123)
                .userId("user")
                .secret("secret")
                .captionProviderApiKey("api-key")
                .captionProviderHostname("")
                .build()

        then:
        KalturaClientConfigException ex = thrown()
        ex.message == "Invalid captionProviderHostname: []"
    }
}
