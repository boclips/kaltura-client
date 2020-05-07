package com.boclips.kalturaclient.session

import au.com.dius.pact.consumer.PactVerificationResult
import au.com.dius.pact.consumer.groovy.PactBuilder
import com.boclips.kalturaclient.config.KalturaClientConfig
import spock.lang.Specification

class RestSessionRetrieverTest extends Specification {
    public static final int PORT = 9991

    def "returns a kaltura session with admin permissions"() {
        given:
        SessionRetriever sessionRetriever = new SessionRetriever(
                KalturaClientConfig.builder()
                        .baseUrl(String.format("http://localhost:%d", PORT))
                        .userId("user@kaltura.com")
                        .secret("123")
                        .partnerId("abc")
                        .sessionTtl(8675309)
                        .build()
        )

        when:
        PactVerificationResult result = mockSessionGeneration().runTest() {
            String session = sessionRetriever.fetch()

            assert session == "aSession"
        }

        then:
        assert result == PactVerificationResult.Ok.INSTANCE
    }

    static mockSessionGeneration() {
        def webapp_service = new PactBuilder()
        webapp_service {
            serviceConsumer "KalturaClient"
            hasPactWith "KalturaApi"
            port PORT
            uponReceiving("POST session start")
            withAttributes([
                    method : 'POST',
                    path   : '/api_v3/service/session/action/start',
                    headers: ['Content-Type': 'application/x-www-form-urlencoded'],
                    body   : 'expiry=8675309&format=1&partnerId=abc&secret=123&type=2&userId=user%40kaltura.com'
            ])
            willRespondWith([
                    status : 200,
                    headers: ['Content-Type': 'application/json;charset=UTF-8'],
                    body   : '"aSession"'
            ])
        } as PactBuilder
    }

}
