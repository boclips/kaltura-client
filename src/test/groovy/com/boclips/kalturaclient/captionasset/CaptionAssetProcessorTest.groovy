package com.boclips.kalturaclient.captionasset

import com.boclips.kalturaclient.captionasset.resources.CaptionAssetListResource
import spock.lang.Specification

class CaptionAssetProcessorTest extends Specification {
    def "it doesn't blow up on nulls, which can appear in a stream on JSON parsing errors"() {
        given:
        CaptionAssetListResource listResource = new CaptionAssetListResource(objects: [null], totalCount: 1)
        CaptionAssetProcessor processor = new CaptionAssetProcessor()

        when:
        List<CaptionAsset> assets = processor.processCaptionAssetListResource(listResource)

        then:
        assert assets == []
    }
}
