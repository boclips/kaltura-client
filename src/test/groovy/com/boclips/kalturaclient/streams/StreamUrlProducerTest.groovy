package com.boclips.kalturaclient.streams

import com.boclips.kalturaclient.KalturaClientConfig
import com.boclips.kalturaclient.MediaEntryResource
import com.boclips.kalturaclient.streams.StreamFormat
import com.boclips.kalturaclient.streams.StreamUrlProducer
import com.boclips.kalturaclient.streams.StreamUrls
import spock.lang.Specification

class StreamUrlProducerTest extends Specification {
    def "provides stream urls for all popular formats"() {
        given:
        MediaEntryResource mediaEntryResource = new MediaEntryResource("id", "referenceId")
        KalturaClientConfig config = KalturaClientConfig.builder()
                .partnerId("12313123")
                .userId("irrelevant")
                .secret("irrelevant")
                .build()

        when:
        StreamUrls urls = new StreamUrlProducer(config).convert(mediaEntryResource)

        then:
        urls.withFormat(StreamFormat.APPLE_HDS) == 'https://cdnapisec.kaltura.com/p/12313123/sp/1231312300/playManifest/entryId/id/format/applehttp/protocol/https/video.mp4'
    }
}
