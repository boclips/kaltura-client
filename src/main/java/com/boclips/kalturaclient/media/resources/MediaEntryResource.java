package com.boclips.kalturaclient.media.resources;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MediaEntryResource {
    private String id;
    private String referenceId;
    private String downloadUrl;
    private Integer duration;
    public int status;
}
