package com.boclips.kalturaclient.media.streams

import spock.lang.Specification

class StreamUrlsTest extends Specification {

    def "withFormat returns a correct stream url"() {
        when:
        StreamUrls urls = new StreamUrls("https://kaltura.com/stream/[FORMAT]/")

        then:
        urls.withFormat(StreamFormat.MPEG_DASH) == "https://kaltura.com/stream/mpegdash/"
        urls.withFormat(StreamFormat.APPLE_HDS) == "https://kaltura.com/stream/applehttp/"
        urls.withFormat(StreamFormat.PROGRESSIVE_DOWNLOAD) == "https://kaltura.com/stream/url/"
    }
}
