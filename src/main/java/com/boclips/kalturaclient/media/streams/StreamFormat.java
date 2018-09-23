package com.boclips.kalturaclient.media.streams;

public enum StreamFormat {
    PROGRESSIVE_DOWNLOAD("url"),
    MPEG_DASH("mpegdash"),
    APPLE_HDS("applehttp");

    private String code;

    StreamFormat(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
