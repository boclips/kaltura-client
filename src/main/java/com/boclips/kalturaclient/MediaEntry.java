package com.boclips.kalturaclient;

import com.boclips.kalturaclient.streams.StreamUrls;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MediaEntry {
    private final String id;
    private final String referenceId;
    private final StreamUrls streams;
}