package com.boclips.kalturaclient.media

import com.boclips.kalturaclient.http.KalturaClientApiException
import com.boclips.kalturaclient.http.ResponseObjectType
import com.boclips.kalturaclient.media.links.LinkBuilder
import com.boclips.kalturaclient.media.resources.MediaEntryResource
import com.boclips.kalturaclient.media.resources.MediaListResource
import com.boclips.kalturaclient.media.streams.StreamFormat
import com.boclips.kalturaclient.media.streams.StreamUrlProducer
import spock.lang.Specification
import test_support.Factories

import java.time.Duration

class MediaProcessorTest extends Specification {
    private MediaListResource resource
    private MediaProcessor processor

    def setup() {
        processor = new MediaProcessor(
                new StreamUrlProducer(Factories.exampleConfig()),
                new LinkBuilder(Factories.exampleConfig())
        )

        MediaEntryResource mediaEntryResource = MediaEntryResource.builder()
                .id("123")
                .referenceId("ref-123")
                .downloadUrl("http://kaltura.com/download/123.mp4")
                .duration(120)
                .status(2)
                .build()
        resource = MediaListResource.builder()
                .objectType(ResponseObjectType.KALTURA_MEDIA_LIST_RESPONSE.type)
                .code("code")
                .totalCount(1L)
                .objects(Arrays.asList(mediaEntryResource))
                .build()
    }

    def "process MediaEntryResource to MediaEntry"() {
        when:
        List<MediaEntry> mediaEntry = processor.process(resource)

        then:
        mediaEntry[0].id == "123"
        mediaEntry[0].referenceId == "ref-123"
        mediaEntry[0].downloadUrl == "http://kaltura.com/download/123.mp4"
        mediaEntry[0].duration == Duration.ofMinutes(2)
        mediaEntry[0].status == MediaEntryStatus.READY
    }

    def "produces valid streams"() {
        when:
        List<MediaEntry> mediaEntries = processor.process(resource)

        then:
        mediaEntries[0].streams.withFormat(StreamFormat.APPLE_HDS) == "https://cdnapisec.kaltura.com/p/partner-123/sp/partner-12300/playManifest/entryId/123/format/applehttp/protocol/https/video.mp4"
        mediaEntries[0].streams.withFormat(StreamFormat.MPEG_DASH) == "https://cdnapisec.kaltura.com/p/partner-123/sp/partner-12300/playManifest/entryId/123/format/mpegdash/protocol/https/video.mp4"
        mediaEntries[0].streams.withFormat(StreamFormat.PROGRESSIVE_DOWNLOAD) == "https://cdnapisec.kaltura.com/p/partner-123/sp/partner-12300/playManifest/entryId/123/format/url/protocol/https/video.mp4"
    }

    def "produces a valid templated thumbnail url"() {
        when:
        List<MediaEntry> mediaEntries = processor.process(resource)

        then:
        mediaEntries[0].thumbnailUrl == "https://cdnapisec.kaltura.com/p/partner-123/thumbnail/entry_id/123/width/{thumbnailWidth}/vid_slices/3/vid_slice/1"
    }

    def "produces a valid templated video preview url"() {
        when:
        List<MediaEntry> mediaEntries = processor.process(resource)

        then:
        mediaEntries[0].videoPreviewUrl == "https://cdnapisec.kaltura.com/p/partner-123/thumbnail/entry_id/123/width/{thumbnailWidth}/vid_slices/{thumbnailCount}"
    }

    def "throws when the resource is unsuccessful"() {
        when:
        resource = resource.toBuilder().objectType("Bad thing").build()
        processor.process(resource)

        then:
        thrown KalturaClientApiException
    }
}
