package com.boclips.kalturaclient.media.resources;

public enum MediaEntryStatusResource {
    ERROR_IMPORTING(-2),
    ERROR_CONVERTING(-1),
    IMPORT(0),
    PRECONVERT(1),
    READY(2),
    DELETED(3),
    PENDING(4),
    MODERATE(5),
    BLOCKED(6),
    NO_CONTENT(7),
    INVALID(8);

    private final int status;

    MediaEntryStatusResource(int status) {
        this.status = status;
    }

    public static MediaEntryStatusResource fromInteger(int x) {
        MediaEntryStatusResource[] values = MediaEntryStatusResource.values();

        for (MediaEntryStatusResource value : values) {
            if (value.status == x) {
                return value;
            }
        }

        return INVALID;
    }
}