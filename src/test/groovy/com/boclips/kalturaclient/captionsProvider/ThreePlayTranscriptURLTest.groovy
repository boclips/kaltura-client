package com.boclips.kalturaclient.captionsProvider

import spock.lang.Specification

class ThreePlayTranscriptURLTest extends Specification {
    def 'returns valid url for asset'() {
        given:
        def config = new CaptionProviderConfig("superKey", "api.3playmedia.com")
        def threePlayURL = new ThreePlayTranscriptURL(config)

        when:
        def url = threePlayURL.getUrlFor("superAsset")

        then:
        url.get().toString() == "https://api.3playmedia.com/v3/transcripts?api_key=superKey&media_file_name=superAsset"
    }

    def 'returns an empty optional on error'() {
        given:
        def config = new CaptionProviderConfig("superKey", "\n")
        def threePlayURL = new ThreePlayTranscriptURL(config)

        when:
        def url = threePlayURL.getUrlFor("superAsset")

        then:
        url.isEmpty() == true
    }
}
