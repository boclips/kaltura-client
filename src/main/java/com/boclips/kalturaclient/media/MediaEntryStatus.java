package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.media.resources.MediaEntryStatusResource;

public enum MediaEntryStatus {
    READY,
    NOT_READY;

    public static MediaEntryStatus from(MediaEntryStatusResource statusResource) {
        switch (statusResource) {
            case READY:
                return READY;
            default:
                return NOT_READY;

        }
    }
}
