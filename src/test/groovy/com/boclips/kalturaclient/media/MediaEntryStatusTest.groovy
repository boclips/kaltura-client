package com.boclips.kalturaclient.media

import com.boclips.kalturaclient.media.resources.MediaEntryStatusResource
import spock.lang.Specification

class MediaEntryStatusTest extends Specification {

    def "converts ready status resource to ready status"() {
        when:
        MediaEntryStatus status = MediaEntryStatus.from(MediaEntryStatusResource.READY)
        then:
        status == MediaEntryStatus.READY
    }

    def "converts any other status resource to not ready"() {
        when:
        MediaEntryStatus errorImport = MediaEntryStatus.from(MediaEntryStatusResource.ERROR_IMPORTING)
        MediaEntryStatus errorConverting = MediaEntryStatus.from(MediaEntryStatusResource.ERROR_CONVERTING)
        MediaEntryStatus importStatus = MediaEntryStatus.from(MediaEntryStatusResource.IMPORT)
        MediaEntryStatus deleted = MediaEntryStatus.from(MediaEntryStatusResource.DELETED)
        MediaEntryStatus pending = MediaEntryStatus.from(MediaEntryStatusResource.PENDING)
        MediaEntryStatus moderate = MediaEntryStatus.from(MediaEntryStatusResource.MODERATE)
        MediaEntryStatus blocked = MediaEntryStatus.from(MediaEntryStatusResource.BLOCKED)
        MediaEntryStatus noContent = MediaEntryStatus.from(MediaEntryStatusResource.NO_CONTENT)
        MediaEntryStatus invalid = MediaEntryStatus.from(MediaEntryStatusResource.INVALID)

        List<MediaEntryStatus> statuses = Arrays.asList(
                errorImport,
                errorConverting,
                importStatus,
                deleted,
                pending,
                moderate,
                blocked,
                noContent,
                invalid
        )
        then:
        statuses.forEach { it == MediaEntryStatus.NOT_READY  }
    }
}
