package com.boclips.kalturaclient.captionsProvider;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.util.Arrays.stream;

@Getter
@AllArgsConstructor
public enum CaptionProviderCaptionStatus {
    IN_PROGRESS, COMPLETE, CANCELLED, UNKNOWN;

    public static CaptionProviderCaptionStatus extract(String status) {
        return stream(values())
            .filter(it -> it.name().equalsIgnoreCase(status))
            .findFirst()
            .orElse(UNKNOWN);
    }
}
