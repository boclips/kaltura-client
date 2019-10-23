package com.boclips.kalturaclient.flavorAsset.resources;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlavorAssetListResource {
    public List<FlavorAssetResource> objects;
    public Integer totalCount;
}
