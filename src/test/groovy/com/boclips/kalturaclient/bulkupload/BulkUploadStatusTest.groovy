package com.boclips.kalturaclient.bulkupload

import spock.lang.Specification
import spock.lang.Unroll

class BulkUploadStatusTest extends Specification {

    @Unroll
    def "Kaltura status #code is #status"() {
        expect:
        BulkUploadStatus.fromCode(code) == status
        status.hasEnded() == ended

        where:
        code | status                                  | ended
        0    | BulkUploadStatus.PENDING                | false
        1    | BulkUploadStatus.QUEUED                 | false
        2    | BulkUploadStatus.PROCESSING             | false
        3    | BulkUploadStatus.PROCESSED              | false
        4    | BulkUploadStatus.MOVEFILE               | false
        5    | BulkUploadStatus.FINISHED               | true
        6    | BulkUploadStatus.FAILED                 | true
        7    | BulkUploadStatus.ABORTED                | true
        8    | BulkUploadStatus.ALMOST_DONE            | false
        9    | BulkUploadStatus.RETRY                  | false
        10   | BulkUploadStatus.FATAL                  | true
        11   | BulkUploadStatus.DONT_PROCESS           | false
        12   | BulkUploadStatus.FINISHED_PARTIALLY     | true
        13   | BulkUploadStatus.SUSPEND                | false
        14   | BulkUploadStatus.SUSPEND_ALMOST_DONE    | false
    }

    def "rejects an unknown status"() {
        when:
        BulkUploadStatus.fromCode(99)

        then:
        thrown(IllegalArgumentException)
    }
}
