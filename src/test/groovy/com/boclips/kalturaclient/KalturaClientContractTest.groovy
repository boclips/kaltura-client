package com.boclips.kalturaclient

import au.com.dius.pact.consumer.PactVerificationResult
import au.com.dius.pact.consumer.groovy.PactBodyBuilder
import au.com.dius.pact.consumer.groovy.PactBuilder
import spock.lang.Specification

class KalturaClientContractTest extends Specification {

    def "returns a session"() {
        given:
        KalturaClient kalturaClient = new KalturaClient("http://localhost:9999")

        when:
        PactVerificationResult result = mockTransactionSteps().runTest() {
            KalturaSession session = kalturaClient.generateSession()

            assert session.toString() == "\"aSession\""
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
                    body   : PactBodyBuilder.regexp(~/^"[a-zA-Z0-9=]*"$/, '"aSession"')
            ])
        } as PactBuilder
    }
}

