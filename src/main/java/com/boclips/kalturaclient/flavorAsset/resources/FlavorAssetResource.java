package com.boclips.kalturaclient.flavorAsset.resources;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlavorAssetResource {
    private String id;
    private Integer size;
    private Integer flavorParamsId;
    private String entryId;
    private Boolean isOriginal;
    private Integer width;
    private Integer height;
}
