package com.boclips.kalturaclient

import au.com.dius.pact.consumer.PactVerificationResult
import au.com.dius.pact.consumer.groovy.PactBuilder
import com.boclips.kalturaclient.session.KalturaSession
import com.boclips.kalturaclient.session.SessionGenerator
import spock.lang.Specification

import java.time.Instant

class KalturaClientIntegrationTest extends Specification {

    def "returns a list of media entries filtered by reference id"() {
        given:
        KalturaClientConfig config = KalturaClientConfig.builder()
                .baseUrl("http://localhost:9999")
                .partnerId("abc")
                .secret("123")
                .userId("user@kaltura.com")
                .build()

        SessionGenerator sessionGenerator = Mock(SessionGenerator)
        sessionGenerator.get() >> new KalturaSession("123", Instant.now())

        KalturaClient kalturaClient = new KalturaClient(config, sessionGenerator)

        when:
        PactVerificationResult result = mockMediaList().runTest() {
            List<MediaEntry> mediaEntries = kalturaClient.mediaEntriesByReferenceIds("2526940")

            MediaEntry mediaEntry = mediaEntries.get(0)
            assert mediaEntry.id == "_1234assd"
        }

        then:
        assert result == PactVerificationResult.Ok.INSTANCE
    }

    static mockMediaList() {
        def webapp_service = new PactBuilder()
        webapp_service {
            serviceConsumer "KalturaClient"
            hasPactWith "KalturaApi"
            port 9999
            uponReceiving("GET media list by reference ids")
            withAttributes([
                    method: 'GET',
                    path  : '/api_v3/service/media/action/list',
                    query : [
                            'filter[referenceIdIn]': '2526940',
                            'ks'                   : '123',
                            'format'               : '1'
                    ]
            ])
            willRespondWith([
                    status : 200,
                    headers: ['Content-Type': 'application/json'],
            ])
            withBody {
                objects eachLike(1, {
                    id identifier('_1234assd')
                })
            }
        } as PactBuilder
    }
}

