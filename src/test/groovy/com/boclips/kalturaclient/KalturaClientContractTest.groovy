package com.boclips.kalturaclient

import au.com.dius.pact.consumer.PactVerificationResult
import au.com.dius.pact.consumer.groovy.PactBuilder
import spock.lang.Specification

class KalturaClientContractTest extends Specification {

    def "returns a session"() {
        given:
        KalturaClientConfig config = KalturaClientConfig.builder()
                .baseUrl("http://localhost:9999")
                .partnerId("abc")
                .secret("123")
                .userId("user@kaltura.com")
                .build()
        KalturaClient kalturaClient = new KalturaClient(config)

        when:
        PactVerificationResult result = mockTransactionSteps().runTest() {
            KalturaSession session = kalturaClient.generateSession(86400)

            assert session.token == "aSession"
        }

        then:
        assert result == PactVerificationResult.Ok.INSTANCE
    }

    static mockTransactionSteps() {
        def webapp_service = new PactBuilder()
        webapp_service {
            serviceConsumer "KalturaClient"
            hasPactWith "KalturaApi"
            port 9999
            uponReceiving("POST session start")
            withAttributes([
                    method : 'POST',
                    path   : '/api_v3/service/session/action/start',
                    headers: ['Content-Type': 'application/x-www-form-urlencoded'],
                    body   : 'expiry=86400&format=1&partnerId=abc&secret=123&type=0&userId=user%40kaltura.com'
            ])
            willRespondWith([
                    status : 200,
                    headers: ['Content-Type': 'application/json;charset=UTF-8'],
                    body   : '"aSession"'
            ])
        } as PactBuilder
    }
}

