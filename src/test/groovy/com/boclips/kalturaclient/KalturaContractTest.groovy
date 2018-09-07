package com.boclips.kalturaclient

import au.com.dius.pact.model.Pact
import au.com.dius.pact.model.PactReader
import au.com.dius.pact.model.Response
import au.com.dius.pact.provider.ConsumerInfo
import au.com.dius.pact.provider.ProviderClient
import au.com.dius.pact.provider.ProviderInfo
import au.com.dius.pact.provider.ResponseComparison
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class KalturaContractTest extends Specification {

    @Shared
    protected ProviderInfo serviceProvider

    @Shared
    protected Pact consumerPact

    protected Map compareResponses(Map<String, Object> actualResponse, Response mockResponse) {
        return ResponseComparison.compareResponse(
                mockResponse,
                actualResponse,
                actualResponse.statusCode,
                actualResponse.headers,
                actualResponse.data)
    }

    protected static void assertResponseMatch(Map result) {
        assert result.method == true

        if (result.headers.size() > 0) {
            result.headers.each() { k, v ->
                assert v == true
            }
        }

        assert result.body.size() == 0
    }

    protected Object makeProviderRequest(request) {
        ProviderClient providerClient = new ProviderClient()
        providerClient.provider = serviceProvider
        providerClient.request = request
        providerClient.makeRequest()
    }

    def setupSpec() {
        serviceProvider = new ProviderInfo("KalturaApi")
        serviceProvider.protocol = "https"
        serviceProvider.host = "www.kaltura.com"
        serviceProvider.port = 443
//        serviceProvider.path = "/api_v3/service/session/action/start"

        ConsumerInfo consumer = new ConsumerInfo()
        consumer.name = "KalturaClient"
        consumer.setPactSource(new File("target/pacts/KalturaClient-KalturaApi.json"))
        consumerPact = new PactReader().loadPact(consumer.getPactSource())
    }

    @Unroll
    def "Kaltura Api contract tests"(interaction) {
        given:
        def mockRequest = interaction.request
        Response mockResponse = interaction.response

        when:
        Map actualResponse = makeProviderRequest(mockRequest)
        Map result = compareResponses(actualResponse, mockResponse)

        then:
        assertResponseMatch(result)

        where:
        interaction << consumerPact.interactions
    }

}
