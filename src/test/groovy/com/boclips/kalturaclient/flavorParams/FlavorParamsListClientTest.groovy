package com.boclips.kalturaclient.flavorParams


import com.boclips.kalturaclient.flavorParams.resources.FlavorParamsListResource
import com.boclips.kalturaclient.http.KalturaRestClient
import spock.lang.Specification

import static com.boclips.kalturaclient.testsupport.TestFactories.*

class FlavorParamsListClientTest extends Specification {

    def "fetches all flavors from Kaltura"() {
        given:
        KalturaRestClient httpClient = Mock(KalturaRestClient)
        FlavorParamsListClient client = new FlavorParamsListClient(httpClient)

        when:
        List<FlavorParams> flavors = client.get()

        then:
        1 * httpClient.get(
                "/flavorparams/action/list",
                _,
                FlavorParamsListResource.class
        ) >> FlavorParamsListResourceFactory.sample(
                Arrays.asList(
                        FlavorParamResourceFactory.sample(320, 100, 1111),
                        FlavorParamResourceFactory.sample(720, 1000, 2222),
                        FlavorParamResourceFactory.sample(0, 0, 3333, 0),
                        FlavorParamResourceFactory.sample(1080, 2500, 4444),
                        FlavorParamResourceFactory.sample(0, 2500, 5555, 1024, "KalturaLiveParams"),
                )
        )

        flavors.size() == 3

        flavors[0].id == 1111
        flavors[1].id == 2222
        flavors[2].id == 4444
    }

    def "can handle nulls"() {
        given:
        KalturaRestClient httpClient = Mock(KalturaRestClient)
        FlavorParamsListClient client = new FlavorParamsListClient(httpClient)

        when:
        List<FlavorParams> flavors = client.get()

        then:
        1 * httpClient.get(
                "/flavorparams/action/list",
                _,
                FlavorParamsListResource.class
        ) >> null

        flavors.size() == 0
    }
}
