package com.boclips.kalturaclient.media;

import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Builder
public class MediaEntry {
    private final String id;
    private final String referenceId;
    private final String downloadUrl;
    private final Duration duration;
    private final MediaEntryStatus status;
    private final ZonedDateTime createdAt;
    private final int conversionProfileId;
    private final List<String> tags;
    private final int playCount;
    private final List<String> flavorParamsIds;
    private final String name;
    private final int width;
    private final int height;


    @Override
    public String toString() {
        return "MediaEntry{id='" + id + '}';
    }
}
