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
    public long createdAt;
    public int conversionProfileId;
    public String tags;
    public int plays;
    public String flavorParamsIds;
    public String name;
    public int width;
    public int height;
}
