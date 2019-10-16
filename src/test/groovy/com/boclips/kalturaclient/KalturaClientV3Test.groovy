package com.boclips.kalturaclient

import com.boclips.kalturaclient.flavorParams.resources.FlavorParamsListResource
import com.boclips.kalturaclient.http.HttpClient
import spock.lang.Specification

import static com.boclips.kalturaclient.testsupport.TestFactories.FlavorParamsListResourceFactory

class KalturaClientV3Test extends Specification {

    def "it loads the project flavorParams on instantiation"() {
        given:
        HttpClient httpClient = Mock(HttpClient)
        KalturaClientConfig config = KalturaClientConfig.builder()
                .partnerId("partner-id")
                .userId("user-id")
                .secret("secret")
                .baseUrl("common://kaltura.com/api")
                .sessionTtl(120)
                .build()

        FlavorParamsListResource flavorParamsListResource = FlavorParamsListResourceFactory.sample()

        when:
        KalturaClientV3 kalturaClient = new KalturaClientV3(httpClient, config)

        then:
        1 * httpClient.get("/flavorparams/action/list", _, _) >> flavorParamsListResource

        kalturaClient.getFlavorParams().get(0).getId() == flavorParamsListResource.objects.get(0).id
        kalturaClient.getFlavorParams().get(0).getHeight() == flavorParamsListResource.objects.get(0).height
        kalturaClient.getFlavorParams().get(0).getWidth() == flavorParamsListResource.objects.get(0).width
    }

}
