package com.boclips.kalturaclient.testsupport

import com.boclips.kalturaclient.flavorParams.FlavorParams
import com.boclips.kalturaclient.flavorParams.Quality
import com.boclips.kalturaclient.flavorParams.resources.FlavorParamsListResource
import com.boclips.kalturaclient.flavorParams.resources.FlavorParamsResource

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

    static class FlavorParamsListFactory {
        static List<FlavorParams> sample() {
            return Arrays.asList(
                    FlavorParams.builder()
                            .id(1111)
                            .width(0)
                            .height(320)
                            .quality(Quality.LOW)
                            .build(),
                    FlavorParams.builder()
                            .id(2222)
                            .width(0)
                            .height(720)
                            .quality(Quality.MEDIUM)
                            .build(),
                    FlavorParams.builder()
                            .id(3333)
                            .width(0)
                            .height(1080)
                            .quality(Quality.HIGH)
                            .build(),
            )
        }
    }

}
