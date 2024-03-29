package com.boclips.kalturaclient;

import com.boclips.kalturaclient.flavorAsset.Asset;

import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface KalturaVideoAssetManager {
    List<Asset> getVideoAssets(String entryId);
    Map<String, List<Asset>> getVideoAssets(Collection<String> entryIds);
    void deleteVideoAsset(String assetId);
    URI getDownloadAssetUrl(String assetId, Boolean includeSession);
}
