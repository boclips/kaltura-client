package com.boclips.kalturaclient.flavorAsset;

import com.boclips.kalturaclient.http.RequestFilters;

import java.net.URI;
import java.net.URL;
import java.util.List;

public interface FlavorAssetGetDownloadUrl {
    URI getDownloadUrl(String assetId, Boolean includeSession);
}
