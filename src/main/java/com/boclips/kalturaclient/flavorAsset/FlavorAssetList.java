package com.boclips.kalturaclient.flavorAsset;

import com.boclips.kalturaclient.http.RequestFilters;

import java.util.List;

public interface FlavorAssetList {
    List<FlavorAsset> list(RequestFilters filters);
}
