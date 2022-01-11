package com.boclips.kalturaclient.baseentry

import com.boclips.kalturaclient.CaptionStatus
import com.boclips.kalturaclient.captionasset.CaptionAsset
import com.boclips.kalturaclient.captionsProvider.CaptionProvider
import spock.lang.Specification

import static com.boclips.kalturaclient.captionsProvider.CaptionProviderCaptionStatus.CANCELLED
import static com.boclips.kalturaclient.captionsProvider.CaptionProviderCaptionStatus.IN_PROGRESS

class BaseEntryWithCaptionsTest extends Specification {

    def 'caption status is HUMAN_GENERATED_AVAILABLE when has human-generated captions'() {
        given:
        def entryWithCaptions = BaseEntryWithCaptions.builder()
                .id("entry_id")
                .baseEntry(BaseEntry.builder().id("entry_id").build())
                .captions(Arrays.asList(CaptionAsset.builder().label("hooman").build()))
                .build()

        when:
        def status = entryWithCaptions.getCaptionStatus()

        then:
        status == CaptionStatus.HUMAN_GENERATED_AVAILABLE
    }

    def 'caption status is UNKNOWN when base entry is missing and no human-generated captions are present'() {
        given:
        def entryWithCaptions = BaseEntryWithCaptions.builder()
                .id("entryId")
                .baseEntry(null)
                .captions(null)
                .build()

        when:
        def status = entryWithCaptions.getCaptionStatus()

        then:
        status == CaptionStatus.UNKNOWN
    }

    def 'caption status is REQUESTED when base entry is tagged with caption providers upload request tag'() {
        given:
        def captionProvider = Mock(CaptionProvider)
        captionProvider.getUploadRequestTag() >> "request-tag"
        def entryWithCaptions = BaseEntryWithCaptions.builder()
                .id("entry_id")
                .baseEntry(BaseEntry.builder().id("entry_id").tags(Arrays.asList("request-tag")).build())
                .captionProvider(captionProvider)
                .build()

        when:
        def status = entryWithCaptions.getCaptionStatus()

        then:
        status == CaptionStatus.REQUESTED
    }

    def 'caption status is NOT_AVAILABLE when video is uploaded to caption provider but its caption provider status is cancelled'() {
        given:
        def captionProvider = Mock(CaptionProvider)
        captionProvider.uploadedToProviderTag() >> "video-uploaded"
        captionProvider.getCaptionStatus("some-video", "entry_id") >> CANCELLED
        def entryWithCaptions = BaseEntryWithCaptions.builder()
                .id("entry_id")
                .baseEntry(BaseEntry
                        .builder()
                        .id("entry_id")
                        .name("some-video")
                        .categories(Arrays.asList("video-uploaded"))
                        .build())
                .captionProvider(captionProvider)
                .build()

        when:
        def status = entryWithCaptions.getCaptionStatus()

        then:
        status == CaptionStatus.NOT_AVAILABLE
    }

    def 'caption status is PROCESSING when video is uploaded to caption provider and caption provider status is not cancelled'() {
        given:
        def captionProvider = Mock(CaptionProvider)
        captionProvider.uploadedToProviderTag() >> "video-uploaded"
        captionProvider.getCaptionStatus("some-video", "entry_id") >> IN_PROGRESS
        def entryWithCaptions = BaseEntryWithCaptions.builder()
                .id("entry_id")
                .baseEntry(BaseEntry
                        .builder()
                        .id("entry_id")
                        .name("some-video")
                        .categories(Arrays.asList("video-uploaded"))
                        .build())
                .captionProvider(captionProvider)
                .build()

        when:
        def status = entryWithCaptions.getCaptionStatus()

        then:
        status == CaptionStatus.PROCESSING
    }

    def 'status is AUTO_GENERATED_AVAILABLE when video is not processing and has some no human-generated captions'() {
        given:
        def captionProvider = Mock(CaptionProvider)
        def entryWithCaptions = BaseEntryWithCaptions.builder()
                .id("entry_id")
                .baseEntry(BaseEntry
                        .builder()
                        .id("entry_id")
                        .name("some-video")
                        .build())
                .captionProvider(captionProvider)
                .captions(Arrays.asList(CaptionAsset.builder().label("(auto-generated)").build()))
                .build()

        when:
        def status = entryWithCaptions.getCaptionStatus()

        then:
        status == CaptionStatus.AUTO_GENERATED_AVAILABLE
    }

    def 'status is UNKNOWN when video is tagged with nothing content provider related and no captions available'() {
        given:
        def captionProvider = Mock(CaptionProvider)
        def entryWithCaptions = BaseEntryWithCaptions.builder()
                .id("entry_id")
                .baseEntry(BaseEntry
                        .builder()
                        .id("entry_id")
                        .name("some-video")
                        .tags(Arrays.asList("mondays"))
                        .build())
                .captionProvider(captionProvider)
                .build()

        when:
        def status = entryWithCaptions.getCaptionStatus()

        then:
        status == CaptionStatus.UNKNOWN
    }

    def 'status is NOT_AVAILABLE when video is not tagged and no captions are available'() {
        given:
        def captionProvider = Mock(CaptionProvider)
        def entryWithCaptions = BaseEntryWithCaptions.builder()
                .id("entry_id")
                .baseEntry(BaseEntry
                        .builder()
                        .id("entry_id")
                        .name("some-video")
                        .build())
                .captionProvider(captionProvider)
                .build()

        when:
        def status = entryWithCaptions.getCaptionStatus()

        then:
        status == CaptionStatus.NOT_AVAILABLE
    }
}
