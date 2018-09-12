package com.boclips.kalturaclient;

import com.boclips.kalturaclient.streams.StreamUrls;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;

@Getter
@Builder
public class MediaEntry {
    private final String id;
    private final String referenceId;
    private final Duration duration;
    private final StreamUrls streams;
    private final String thumbnailUrl;
}