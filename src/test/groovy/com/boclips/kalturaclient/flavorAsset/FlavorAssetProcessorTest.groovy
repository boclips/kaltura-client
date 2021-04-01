package com.boclips.kalturaclient.flavorAsset

import com.boclips.kalturaclient.flavorAsset.resources.FlavorAssetListResource
import spock.lang.Specification

class FlavorAssetProcessorTest extends Specification {
    def "it doesn't blow up on nulls, which can appear in a stream on JSON parsing errors"() {
        given:
        FlavorAssetListResource listResource = new FlavorAssetListResource(objects: [null], totalCount: 1)
        FlavorAssetProcessor processor = new FlavorAssetProcessor()

        when:
        List<Asset> assets = processor.processFlavorAssetListResource(listResource)

        then:
        assert assets == []
    }
}
