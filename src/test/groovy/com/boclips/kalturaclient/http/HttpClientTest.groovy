package com.boclips.kalturaclient.http

import au.com.dius.pact.consumer.PactVerificationResult
import au.com.dius.pact.consumer.groovy.PactBuilder
import com.boclips.kalturaclient.http.HttpClient
import com.boclips.kalturaclient.media.resources.MediaListResource
import com.boclips.kalturaclient.http.RequestFilters
import spock.lang.Specification

class HttpClientTest extends Specification {

    def "returns a list of media entries filtered by reference id"() {
        given:
        HttpClient httpClient = new HttpClient("http://localhost:9999")
        RequestFilters filters = new RequestFilters().add("filter[referenceIdIn]", "213-123-123,does-not-exist")

        when:
        PactVerificationResult result = mockMediaList().runTest() {
            MediaListResource mediaListResource = httpClient.getMediaListResource("123", filters)

            assert mediaListResource.totalCount == 2
            assert mediaListResource.objects.size() == 1
            assert mediaListResource.objects[0].id == "_1234assd"
            assert mediaListResource.objects[0].referenceId == "213-123-123"
        }

        then:
        assert result == PactVerificationResult.Ok.INSTANCE
    }

    def "handles a Kaltura error gracefully"() {
        given:
        HttpClient httpClient = new HttpClient("http://localhost:9999")
        RequestFilters filters = new RequestFilters().add("filter[referenceIdIn]", "does-not-exist")

        when:
        PactVerificationResult result = mockErroredMediaList().runTest() {
            try {
                httpClient.getMediaListResource("123", filters)
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
                totalCount integer(2)
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

