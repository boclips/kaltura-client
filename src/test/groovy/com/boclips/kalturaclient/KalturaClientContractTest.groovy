package com.boclips.kalturaclient

import au.com.dius.pact.consumer.PactVerificationResult
import au.com.dius.pact.consumer.groovy.PactBodyBuilder
import au.com.dius.pact.consumer.groovy.PactBuilder
import spock.lang.Specification

class KalturaClientContractTest extends Specification {

    def "returns a session"() {
        given:
        KalturaClient kalturaClient = new KalturaClient()

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
                    method: 'POST',
                    path  : '/api_v3/service/session/action/start',
//                    body  : 'secret=9c06b188bfb571ae707509180aed5d91&userId=jacek%40boclips.com&type=0&partnerId=1776261&expiry=86400&format=1'
            ])
            willRespondWith([
                    status : 200,
                    headers: ['Content-Type': 'application/json;charset=UTF-8'],
                    body   : PactBodyBuilder.regexp(~/^"[a-zA-Z0-9=]*"$/, '"aSession"')
            ])
        } as PactBuilder
    }
}

