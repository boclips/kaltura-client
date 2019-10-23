package com.boclips.kalturaclient.flavorAsset

import com.boclips.kalturaclient.flavorAsset.resources.FlavorAssetListResource
import com.boclips.kalturaclient.http.KalturaRestClient
import com.boclips.kalturaclient.http.RequestFilters
import com.boclips.kalturaclient.testsupport.TestFactories
import spock.lang.Specification

class FlavorAssetListClientTest extends Specification {

    def "it can get all of the flavor assets for an entry"() {
        given:
        KalturaRestClient httpClient = Mock(KalturaRestClient)
        FlavorAssetListClient client = new FlavorAssetListClient(httpClient)

        when:
        List<FlavorAsset> flavorAssets = client.list(new RequestFilters())

        then:
        1 * httpClient.get(
                "/flavorasset/action/list",
                _,
                FlavorAssetListResource.class
        ) >> TestFactories.flavorAssetListResource(
                Arrays.asList(
                        TestFactories.flavorAssetResource("flavor-asset-id-1"),
                        TestFactories.flavorAssetResource("flavor-asset-id-2")
                )
        )


        flavorAssets.size() == 2

        flavorAssets[0].id == "flavor-asset-id-1"
        flavorAssets[1].id == "flavor-asset-id-2"
    }

}
