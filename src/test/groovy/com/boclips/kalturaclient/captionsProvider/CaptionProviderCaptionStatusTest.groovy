package com.boclips.kalturaclient.captionsProvider

import spock.lang.Specification

class CaptionProviderCaptionStatusTest extends Specification {

    def 'extracts correct status'() {
        expect: CaptionProviderCaptionStatus.extract(status) == expected

        where:
        status        ||  expected
        "complete"    ||  CaptionProviderCaptionStatus.COMPLETE
        "cancelled"   ||  CaptionProviderCaptionStatus.CANCELLED
        "in_progress" ||  CaptionProviderCaptionStatus.IN_PROGRESS
        "weird_stuff" ||  CaptionProviderCaptionStatus.UNKNOWN
    }
}
