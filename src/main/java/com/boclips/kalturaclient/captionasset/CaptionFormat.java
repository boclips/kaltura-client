package com.boclips.kalturaclient.captionasset;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum CaptionFormat {
    SRT("1"),
    DFXP("2"),
    WEBVTT("3"),
    CAP("4");

    private final String value;

    CaptionFormat(String value) {
        this.value = value;
    }

    public static CaptionFormat fromValue(String value) {
        return Arrays.stream(CaptionFormat.values())
                .filter(format -> format.value.equals(value))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
}
