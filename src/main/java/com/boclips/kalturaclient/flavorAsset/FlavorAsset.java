package com.boclips.kalturaclient.flavorAsset;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FlavorAsset {
    private String id;
    private Integer size;
    private Integer flavorParamsId;
    private String entryId;
    private Boolean isOriginal;
}
