package com.boclips.kalturaclient.flavorAsset;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

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
    private ZonedDateTime createdAt;
}
