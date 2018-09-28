package com.boclips.kalturaclient.media

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
        processor = new MediaProcessor(new StreamUrlProducer(Factories.exampleConfig()))

        MediaEntryResource mediaEntryResource = new MediaEntryResource("123", "ref-123", 120, "http://thumbnail.com/1")
        resource = new MediaListResource(Arrays.asList(mediaEntryResource), "Something", "code", 1L)
    }

    def "process MediaEntryResource to MediaEntry"() {
        when:
        List<MediaEntry> mediaEntry = processor.process(resource)

        then:
        mediaEntry[0].id == "123"
        mediaEntry[0].referenceId == "ref-123"
        mediaEntry[0].duration == Duration.ofMinutes(2)
        mediaEntry[0].thumbnailUrl == 'http://thumbnail.com/1'
    }

    def "produces valid streams"() {
        when:
        List<MediaEntry> mediaEntries = processor.process(resource)

        then:
        mediaEntries[0].streams.withFormat(StreamFormat.APPLE_HDS) == "https://cdnapisec.kaltura.com/p/partner-123/sp/partner-12300/playManifest/entryId/123/format/applehttp/protocol/https/video.mp4"
        mediaEntries[0].streams.withFormat(StreamFormat.MPEG_DASH) == "https://cdnapisec.kaltura.com/p/partner-123/sp/partner-12300/playManifest/entryId/123/format/mpegdash/protocol/https/video.mp4"
        mediaEntries[0].streams.withFormat(StreamFormat.PROGRESSIVE_DOWNLOAD) == "https://cdnapisec.kaltura.com/p/partner-123/sp/partner-12300/playManifest/entryId/123/format/url/protocol/https/video.mp4"
    }
}