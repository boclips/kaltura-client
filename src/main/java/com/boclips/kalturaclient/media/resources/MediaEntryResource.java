package com.boclips.kalturaclient.media.resources;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MediaEntryResource {
    private String id;
    private String referenceId;
    private Integer duration;
    private String thumbnailUrl;
}
