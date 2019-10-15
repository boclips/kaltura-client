package com.boclips.kalturaclient.testsupport

import com.boclips.kalturaclient.flavorParams.resources.FlavorParamsResource
import com.boclips.kalturaclient.flavorParams.resources.FlavorParamsListResource

class TestFactories {

    static class FlavorParamResourceFactory {
        static FlavorParamsResource sample(
                int height,
                int bitrate,
                int id = 1111,
                int width = 0,
                String objectType = "KalturaFlavorParams"
        ) {
            return FlavorParamsResource.builder()
                    .id(id)
                    .width(width)
                    .height(height)
                    .videoBitrate(bitrate)
                    .objectType(objectType)
                    .build()
        }
    }

    static class FlavorParamsListResourceFactory {
        static FlavorParamsListResource sample(
                List<FlavorParamsResource> objects = Arrays.asList(
                        FlavorParamResourceFactory.sample(720, 1000, 1111, 0),
                        FlavorParamResourceFactory.sample(1080, 2500, 2222, 0)
                )
        ) {
            return FlavorParamsListResource.builder().objects(objects).build()
        }
    }

}
