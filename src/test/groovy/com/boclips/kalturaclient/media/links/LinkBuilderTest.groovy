package com.boclips.kalturaclient.media.links

import com.boclips.kalturaclient.KalturaClient
import com.boclips.kalturaclient.config.KalturaClientConfig
import com.boclips.kalturaclient.media.streams.StreamFormat
import spock.lang.Specification

import static com.boclips.kalturaclient.testsupport.TestFactories.FlavorParamsListFactory

class LinkBuilderTest extends Specification {

    private KalturaClient client
    private LinkBuilder linkBuilder
    private StreamUrlSessionGenerator linkSessionGenerator

    def "setup"() {
        client = Mock(KalturaClient) {
            getConfig() >> KalturaClientConfig.builder()
                    .partnerId("partner1")
                    .userId("user")
                    .secret("secret")
                    .captionProviderHostname("hostname.com")
                    .captionProviderApiKey("api-key")
                    .build()
        }
        linkSessionGenerator = Mock(StreamUrlSessionGenerator)
        linkBuilder = new LinkBuilder(client, linkSessionGenerator)
    }

    def "can build thumbnail urls"() {
        given:
        String entryId = "media-entry-id"

        when:
        String thumbnailUrl = linkBuilder.getThumbnailUrl(entryId)

        then:
        thumbnailUrl.contains("entry_id/media-entry-id")
        thumbnailUrl.contains("width/{thumbnailWidth}")
    }

    def "can build the thumbnail url from a second"() {
        given:
        String entryId = "media-entry-id"

        when:
        String thumbnailUrl = linkBuilder.getThumbnailUrlBySecond(entryId, 20)

        then:
        thumbnailUrl.contains("entry_id/media-entry-id/width/{thumbnailWidth}")
        thumbnailUrl.contains("/vid_sec/20")
    }

    def "can build default thumbnail url from a id"() {
        given:
        String entryId = "media-entry-id"

        when:
        String thumbnailUrl = linkBuilder.getDefaultThumbnailUrl(entryId)

        then:
        thumbnailUrl.endsWith("entry_id/media-entry-id/width/{thumbnailWidth}")
    }

    def "can build video preview urls"() {
        given:
        String entryId = "media-entry-id"

        when:
        String thumbnailUrl = linkBuilder.getVideoPreviewUrl(entryId)

        then:
        thumbnailUrl.contains("entry_id/media-entry-id")
        thumbnailUrl.contains("width/{thumbnailWidth}")
        thumbnailUrl.contains("vid_slices/{thumbnailCount}")
    }

    def "can build hls stream urls"() {
        given:
        String entryId = "media-entry-id"
        StreamFormat streamTechnique = StreamFormat.APPLE_HDS

        when:
        String hlsStream = linkBuilder.getStreamUrl(entryId, streamTechnique, false)

        then:
        1 * client.getFlavorParams() >> FlavorParamsListFactory.sample()
        hlsStream.contains("entryId/media-entry-id")
        hlsStream.contains("format/" + streamTechnique.code)
        hlsStream.contains("flavorParamIds/1111%2C2222%2C3333")
        !hlsStream.contains("/ks/")
    }

    def "can build hls stream urls with kaltura session attached"() {
        given:
        String entryId = "media-entry-id"
        StreamFormat streamTechnique = StreamFormat.APPLE_HDS
        linkSessionGenerator.getForEntry("media-entry-id") >> "session-for-media-entry-id"

        when:
        String hlsStream = linkBuilder.getStreamUrl(entryId, streamTechnique, true)

        then:
        1 * client.getFlavorParams() >> FlavorParamsListFactory.sample()
        hlsStream.contains("/ks/session-for-media-entry-id")
    }

    def "throws KalturaSessionException when generating session fails"() {
        given:
        String entryId = "media-entry-id"
        StreamFormat streamTechnique = StreamFormat.APPLE_HDS
        linkSessionGenerator.getForEntry("media-entry-id") >> {throw new RuntimeException("something went wrong")}

        when:
        linkBuilder.getStreamUrl(entryId, streamTechnique, true)

        then:
        1 * client.getFlavorParams() >> FlavorParamsListFactory.sample()
        thrown GenerateKalturaSessionException
    }
}
