package com.boclips.kalturaclient.media

import com.boclips.kalturaclient.http.KalturaClientApiException
import com.boclips.kalturaclient.http.ResponseObjectType
import com.boclips.kalturaclient.media.resources.MediaEntryResource
import com.boclips.kalturaclient.media.resources.MediaListResource
import spock.lang.Specification

import java.time.Duration

class MediaProcessorTest extends Specification {
    private MediaListResource resource
    private MediaProcessor processor

    def setup() {
        processor = new MediaProcessor()

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

    def "throws when the resource is unsuccessful"() {
        when:
        resource = resource.toBuilder().objectType("Bad thing").build()
        processor.process(resource)

        then:
        thrown KalturaClientApiException
    }
}
