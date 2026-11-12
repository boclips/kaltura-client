package com.boclips.kalturaclient.bulkupload;

import java.util.Arrays;

public enum BulkUploadStatus {
    PENDING(0, false),
    QUEUED(1, false),
    PROCESSING(2, false),
    PROCESSED(3, false),
    MOVEFILE(4, false),
    FINISHED(5, true),
    FAILED(6, true),
    ABORTED(7, true),
    ALMOST_DONE(8, false),
    RETRY(9, false),
    FATAL(10, true),
    DONT_PROCESS(11, false),
    FINISHED_PARTIALLY(12, true),
    SUSPEND(13, false),
    SUSPEND_ALMOST_DONE(14, false);

    private final int code;
    private final boolean ended;

    BulkUploadStatus(int code, boolean ended) {
        this.code = code;
        this.ended = ended;
    }

    public static BulkUploadStatus fromCode(int code) {
        return Arrays.stream(values())
                .filter(status -> status.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown bulk upload status " + code));
    }

    public boolean hasEnded() {
        return ended;
    }
}
