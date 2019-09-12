package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.media.streams.StreamUrls;
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
    private final StreamUrls streams;
    /**
     * A templated URL:
     * - thumbnailWidth - width in pixels of the thumbnail to be returned
     */
    private final String thumbnailUrl;
    /**
     * A templated URL:
     * - thumbnailWidth - width in pixels of the thumbnail to be returned
     * - thumbnailCount - number of thumbnails to be returned in image
     */
    private final String videoPreviewUrl;
    private final MediaEntryStatus status;
}
