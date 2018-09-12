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

        HttpKalturaClient kalturaClient = new HttpKalturaClient(config, sessionGenerator)

        when:
        PactVerificationResult result = mockMediaList().runTest() {
            Map<String, MediaEntry> mediaEntries = kalturaClient.mediaEntriesByReferenceIds("213-123-123", "does-not-exist")

            MediaEntry mediaEntry = mediaEntries['213-123-123']
            assert mediaEntry.id == "_1234assd"
            assert mediaEntry.referenceId == "213-123-123"

            assert mediaEntries['does-not-exist'] == null
        }

        then:
        assert result == PactVerificationResult.Ok.INSTANCE
    }

    def "handles a Kaltura error gracefully"() {
        given:
        KalturaClientConfig config = KalturaClientConfig.builder()
                .baseUrl("http://localhost:9999")
                .partnerId("abc")
                .secret("123")
                .userId("user@kaltura.com")
                .build()

        SessionGenerator sessionGenerator = Mock(SessionGenerator)
        sessionGenerator.get() >> new KalturaSession("123", Instant.now())

        HttpKalturaClient kalturaClient = new HttpKalturaClient(config, sessionGenerator)

        when:
        PactVerificationResult result = mockErroredMediaList().runTest() {
            try {
                kalturaClient.mediaEntriesByReferenceIds("does-not-exist")
                assert false
            } catch (Exception ex) {
                assert ex.message == "Error in Kaltura request: INVALID_KS"
            }
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
                            'filter[referenceIdIn]': '213-123-123,does-not-exist',
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
                    referenceId identifier('213-123-123')
                    duration 214
                })
                objectType string('KalturaMediaListResponse')
            }
        } as PactBuilder
    }

    static mockErroredMediaList() {
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
                            'filter[referenceIdIn]': 'does-not-exist',
                            'ks'                   : '123',
                            'format'               : '1'
                    ]
            ])
            willRespondWith([
                    status : 200,
                    headers: ['Content-Type': 'application/json'],
            ])
            withBody {
                code string('INVALID_KS')
                objectType string('KalturaAPIException')
            }
        } as PactBuilder
    }
}