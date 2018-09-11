package com.boclips.kalturaclient;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MediaEntry {
    private final String id;
    private final String referenceId;
}
