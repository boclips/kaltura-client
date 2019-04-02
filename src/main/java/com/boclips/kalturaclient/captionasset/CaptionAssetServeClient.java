package com.boclips.kalturaclient.captionasset;

import com.boclips.kalturaclient.KalturaClientConfig;
import com.boclips.kalturaclient.http.HttpClient;

public class CaptionAssetServeClient implements CaptionAssetServe {
    private final HttpClient client;

    public CaptionAssetServeClient(KalturaClientConfig config) {
        this.client = new HttpClient(config.getBaseUrl());
    }

    @Override
    public String get(String sessionToken, String assetId) {
        return client.serveCaptionAsset(sessionToken, assetId);
    }
}
