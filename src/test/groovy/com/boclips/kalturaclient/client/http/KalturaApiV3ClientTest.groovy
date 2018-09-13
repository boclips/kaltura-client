package com.boclips.kalturaclient.client.http

import au.com.dius.pact.consumer.PactVerificationResult
import au.com.dius.pact.consumer.groovy.PactBuilder
import com.boclips.kalturaclient.MediaEntry
import spock.lang.Specification

class KalturaApiV3ClientTest extends Specification {

    def "returns a list of media entries filtered by reference id"() {
        given:
        KalturaApiV3Client kalturaClient = new KalturaApiV3Client("http://localhost:9999")

        when:
        PactVerificationResult result = mockMediaList().runTest() {
            List<MediaEntry> mediaEntries = kalturaClient.getMediaActionList("123", Arrays.asList("213-123-123", "does-not-exist"))

            assert mediaEntries.size() == 1
            assert mediaEntries[0].id == "_1234assd"
            assert mediaEntries[0].referenceId == "213-123-123"
        }

        then:
        assert result == PactVerificationResult.Ok.INSTANCE
    }

    def "handles a Kaltura error gracefully"() {
        given:
        KalturaApiV3Client kalturaClient = new KalturaApiV3Client("http://localhost:9999")

        when:
        PactVerificationResult result = mockErroredMediaList().runTest() {
            try {
                kalturaClient.getMediaActionList("123", Arrays.asList("does-not-exist"))
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

