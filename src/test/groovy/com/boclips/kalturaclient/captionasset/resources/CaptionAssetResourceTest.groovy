package com.boclips.kalturaclient.captionasset.resources

import com.boclips.kalturaclient.captionasset.CaptionFormat
import com.boclips.kalturaclient.captionasset.KalturaLanguage
import spock.lang.Specification

class CaptionAssetResourceTest extends Specification {

    def resource = CaptionAssetResource.builder()
            .id("the id")
            .format(CaptionFormat.WEBVTT.value)
            .label("the label")
            .language("French")
            .build()

    def "ToAsset"() {
        when:
        def asset = resource.toAsset()

        then:
        asset.id == "the id"
        asset.fileType == CaptionFormat.WEBVTT
        asset.label == "the label"
        asset.language == KalturaLanguage.FRENCH
    }

    def "Throws when id is null"() {
        when:
        resource.toBuilder().id(null).build().toAsset()

        then:
        thrown IllegalStateException
    }

    def "Throws when id is empty"() {
        when:
        resource.toBuilder().id("").build().toAsset()

        then:
        thrown IllegalStateException
    }

    def "Throws when label is null"() {
        when:
        resource.toBuilder().label(null).build().toAsset()

        then:
        thrown IllegalStateException
    }

    def "Throws when label is empty"() {
        when:
        resource.toBuilder().label("").build().toAsset()

        then:
        thrown IllegalStateException
    }

    def "Throws when language is null"() {
        when:
        resource.toBuilder().language(null).build().toAsset()

        then:
        thrown IllegalStateException
    }

    def "Throws when language is empty"() {
        when:
        resource.toBuilder().language("").build().toAsset()

        then:
        thrown IllegalStateException
    }

    def "Throws when format is null"() {
        when:
        resource.toBuilder().format(null).build().toAsset()

        then:
        thrown IllegalStateException
    }

    def "Throws when format is empty"() {
        when:
        resource.toBuilder().format("").build().toAsset()

        then:
        thrown IllegalStateException
    }
}
