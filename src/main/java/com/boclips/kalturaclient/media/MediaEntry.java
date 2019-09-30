package com.boclips.kalturaclient.media;

import lombok.Builder;
import lombok.Getter;

import java.time.Duration;

@Getter
@Builder
public class MediaEntry {
    private final String id;
    private final String referenceId;
    private final String downloadUrl;
    private final Duration duration;
    private final MediaEntryStatus status;
}
