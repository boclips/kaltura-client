package com.boclips.kalturaclient.captionsProvider

import spock.lang.Shared
import spock.lang.Specification

import static com.boclips.kalturaclient.captionsProvider.CaptionProviderCaptionStatus.*
import static com.boclips.kalturaclient.captionsProvider.CaptionStatusResponse.CaptionItem.CaptionItemBuilder

class ThreePlayCaptionProviderTest extends Specification {

    def correctResponse = new CaptionStatusResponse.CaptionStatusResponseBuilder()
            .code(200)
            .data(Arrays.asList(new CaptionItemBuilder().status("complete").referenceId("1_2hmsi77t").build()))
            .build()

    def multipleEntriesResponse = new CaptionStatusResponse.CaptionStatusResponseBuilder()
            .code(200)
            .data(Arrays.asList(
                    new CaptionItemBuilder().status("in_progress").referenceId("1_2hmsi77t").build(),
                    new CaptionItemBuilder().status("cancelled").referenceId("1_2hmsi77t_this").build())
            )
            .build()

    @Shared
    def validTranscriptUrl = new ThreePlayTranscriptURL(new CaptionProviderConfig("api_key", "api.3playmedia.com"))

    def 'successfully returns caption status'() {
        given:
        def httpJsonClientMock = Mock(HttpTypedClient)
        def captionProvider = new ThreePlayCaptionProvider(validTranscriptUrl, httpJsonClientMock)
        def expectedUrl = validTranscriptUrl.getUrlFor("cats and dogs").get()
        1 * httpJsonClientMock.execute(expectedUrl, CaptionStatusResponse.class) >> correctResponse

        when:
        def status = captionProvider.getCaptionStatus("cats and dogs", "1_2hmsi77t")

        then:
        status == COMPLETE
    }

    def 'returns in_progress on http call error'() {
        given:
        def httpJsonClientMock = Mock(HttpTypedClient)
        httpJsonClientMock.execute(_, _) >> { throw new IOException() }
        def captionProvider = new ThreePlayCaptionProvider(validTranscriptUrl, httpJsonClientMock)

        when:
        def status = captionProvider.getCaptionStatus("cats and dogs", "1_2hmsi77t")

        then:
        status == IN_PROGRESS
    }

    def 'returns in_progress when response is invalid'(response) {
        given:
        def httpClientMock = Mock(HttpTypedClient)
        httpClientMock.execute(_, _) >> response
        def captionProvider = new ThreePlayCaptionProvider(validTranscriptUrl, httpClientMock)

        when:
        def status = captionProvider.getCaptionStatus("cats and dogs", "1_2hmsi77t")

        then:
        status == IN_PROGRESS

        where:
        response << [
                new CaptionStatusResponse.CaptionStatusResponseBuilder().code(400).build(),
                new CaptionStatusResponse.CaptionStatusResponseBuilder().code(200).build(),
                new CaptionStatusResponse.CaptionStatusResponseBuilder().code(200).data(Collections.emptyList()).build(),
        ]
    }

    def 'returns IN_PROGRESS when url failed to build'() {
        given:
        def httpClientMock = Mock(HttpTypedClient)
        def threePlayURLMock = Mock(ThreePlayTranscriptURL)
        threePlayURLMock.getUrlFor(_) >> Optional.empty()
        def captionProvider = new ThreePlayCaptionProvider(threePlayURLMock, httpClientMock)

        when:
        def status = captionProvider.getCaptionStatus("doesn't matter", "doesn't matter")

        then:
        status == IN_PROGRESS
    }

    def 'returns status for the requested entry'() {
        given:
        def httpJsonClientMock = Mock(HttpTypedClient)
        httpJsonClientMock.execute(_, _) >> multipleEntriesResponse
        def captionProvider = new ThreePlayCaptionProvider(validTranscriptUrl, httpJsonClientMock)

        when:
        def status = captionProvider.getCaptionStatus("cats and dogs", "1_2hmsi77t_this")

        then:
        status == CANCELLED
    }
}
