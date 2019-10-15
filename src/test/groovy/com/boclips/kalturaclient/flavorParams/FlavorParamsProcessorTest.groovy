package com.boclips.kalturaclient.flavorParams


import com.boclips.kalturaclient.flavorParams.resources.FlavorParamsResource
import com.boclips.kalturaclient.flavorParams.resources.FlavorParamsListResource
import spock.lang.Specification

import static com.boclips.kalturaclient.testsupport.TestFactories.*

class FlavorParamsProcessorTest extends Specification {

    def "Processes a flavorParam list to a List of Flavors"() {
        given:
        FlavorParamsResource flavorParamOne = FlavorParamResourceFactory.sample(260, 800, 1111)
        FlavorParamsResource flavorParamTwo = FlavorParamResourceFactory.sample(768, 800, 2222)

        FlavorParamsListResource listResource = FlavorParamsListResource.builder().
                objects(Arrays.asList(flavorParamOne, flavorParamTwo))
                .totalCount(2)
                .build()

        when:
        FlavorParamsProcessor processor = new FlavorParamsProcessor()
        List<FlavorParams> resultingFlavors = processor.process(listResource)

        then:
        resultingFlavors.size() == 2

        resultingFlavors[0].id == 1111
        resultingFlavors[0].height == 260
        resultingFlavors[0].width == 0

        resultingFlavors[1].id == 2222
        resultingFlavors[1].height == 768
        resultingFlavors[1].width == 0
    }

    def "it filters out non-KalturaFlavorParam objects"() {
        given:
        FlavorParamsResource flavorParamOne = FlavorParamResourceFactory.sample(260, 100, 1111, 0)
        FlavorParamsResource flavorParamTwo = FlavorParamResourceFactory.sample(768, 1000, 2222, 0, "KalturaLiveParams")

        FlavorParamsListResource listResource = FlavorParamsListResource.builder().
                objects(Arrays.asList(flavorParamOne, flavorParamTwo))
                .totalCount(2)
                .build()

        when:
        FlavorParamsProcessor processor = new FlavorParamsProcessor()
        List<FlavorParams> resultingFlavors = processor.process(listResource)

        then:
        resultingFlavors.size() == 1

        resultingFlavors[0].id == 1111
    }

    def "Processes a low quality flavor (360/400)"() {
        given:
        FlavorParamsResource flavorParamResource = FlavorParamResourceFactory.sample(360, 400)

        when:
        FlavorParamsProcessor processor = new FlavorParamsProcessor()
        FlavorParams resultingFlavor = processor.processFlavorParamResource(flavorParamResource)

        then:
        resultingFlavor.id == 1111
        resultingFlavor.quality == Quality.LOW
    }

    def "Processes a medium quality flavor (540/900)"() {
        given:
        FlavorParamsResource flavorParamResource = FlavorParamResourceFactory.sample(540, 900)

        when:
        FlavorParamsProcessor processor = new FlavorParamsProcessor()
        FlavorParams resultingFlavor = processor.processFlavorParamResource(flavorParamResource)

        then:
        resultingFlavor.id == 1111
        resultingFlavor.quality == Quality.MEDIUM
    }

    def "Processes a medium quality flavor (720/1500)"() {
        given:
        FlavorParamsResource flavorParamResource = FlavorParamResourceFactory.sample(720, 1500)

        when:
        FlavorParamsProcessor processor = new FlavorParamsProcessor()
        FlavorParams resultingFlavor = processor.processFlavorParamResource(flavorParamResource)

        then:
        resultingFlavor.id == 1111
        resultingFlavor.quality == Quality.MEDIUM
    }

    def "Processes a high quality flavor (720/2500)"() {
        given:
        FlavorParamsResource flavorParamResource = FlavorParamResourceFactory.sample(720, 2500)

        when:
        FlavorParamsProcessor processor = new FlavorParamsProcessor()
        FlavorParams resultingFlavor = processor.processFlavorParamResource(flavorParamResource)

        then:
        resultingFlavor.id == 1111
        resultingFlavor.quality == Quality.HIGH
    }

    def "Processes a high quality flavor (1080)"() {
        given:
        FlavorParamsResource flavorParamResource = FlavorParamResourceFactory.sample(1080, 2500)

        when:
        FlavorParamsProcessor processor = new FlavorParamsProcessor()
        FlavorParams resultingFlavor = processor.processFlavorParamResource(flavorParamResource)

        then:
        resultingFlavor.id == 1111
        resultingFlavor.quality == Quality.HIGH
    }

    def "Processes a high quality flavor with auto height, low bitrate"() {
        given:
        FlavorParamsResource flavorParamResource = FlavorParamResourceFactory.sample(0, 200, 1111, 320)

        when:
        FlavorParamsProcessor processor = new FlavorParamsProcessor()
        FlavorParams resultingFlavor = processor.processFlavorParamResource(flavorParamResource)

        then:
        resultingFlavor.id == 1111
        resultingFlavor.quality == Quality.LOW
    }

    def "Processes a high quality flavor with auto height, medium bitrate"() {
        given:
        FlavorParamsResource flavorParamResource = FlavorParamResourceFactory.sample(0, 900, 1111, 768)

        when:
        FlavorParamsProcessor processor = new FlavorParamsProcessor()
        FlavorParams resultingFlavor = processor.processFlavorParamResource(flavorParamResource)

        then:
        resultingFlavor.id == 1111
        resultingFlavor.quality == Quality.MEDIUM
    }

    def "Processes a high quality flavor with auto height, high bitrate"() {
        given:
        FlavorParamsResource flavorParamResource = FlavorParamResourceFactory.sample(0, 2500, 1111, 1024)

        when:
        FlavorParamsProcessor processor = new FlavorParamsProcessor()
        FlavorParams resultingFlavor = processor.processFlavorParamResource(flavorParamResource)

        then:
        resultingFlavor.id == 1111
        resultingFlavor.quality == Quality.HIGH
    }

    def "Return a null, for a LiveParam object"() {
        given:
        FlavorParamsResource flavorParamResource = FlavorParamResourceFactory.sample(0, 2500, 1111, 1024, "KalturaLiveParams")

        when:
        FlavorParamsProcessor processor = new FlavorParamsProcessor()
        FlavorParams resultingFlavor = processor.processFlavorParamResource(flavorParamResource)

        then:
        resultingFlavor == null
    }

    def "Return a null, when height and width is auto"() {
        given:
        FlavorParamsResource flavorParamResource = FlavorParamResourceFactory.sample(0, 123, 1111, 0)

        when:
        FlavorParamsProcessor processor = new FlavorParamsProcessor()
        FlavorParams resultingFlavor = processor.processFlavorParamResource(flavorParamResource)

        then:
        resultingFlavor == null
    }

    def "Return a null, when no bitrate (ie no video)"() {
        given:
        FlavorParamsResource flavorParamResource = FlavorParamResourceFactory.sample(0, 123, 0, 0)

        when:
        FlavorParamsProcessor processor = new FlavorParamsProcessor()
        FlavorParams resultingFlavor = processor.processFlavorParamResource(flavorParamResource)

        then:
        resultingFlavor == null
    }
}
