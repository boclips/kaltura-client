package com.boclips.kalturaclient;

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
}
