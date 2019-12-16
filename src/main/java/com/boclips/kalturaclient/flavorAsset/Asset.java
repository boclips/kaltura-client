package com.boclips.kalturaclient.flavorAsset;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Asset {
    private String id;
    private Integer sizeKb;
    private Integer bitrateKbps;
    private Integer flavorParamsId;
    private String entryId;
    private Boolean isOriginal;
    private Integer width;
    private Integer height;
}
