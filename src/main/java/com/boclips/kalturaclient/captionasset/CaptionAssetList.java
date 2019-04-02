package com.boclips.kalturaclient.captionasset;

import com.boclips.kalturaclient.http.RequestFilters;

import java.util.List;

public interface CaptionAssetList {

    List<CaptionAsset> get(String sessionToken, RequestFilters filters);
}
