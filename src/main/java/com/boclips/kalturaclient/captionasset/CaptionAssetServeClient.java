package com.boclips.kalturaclient.captionasset;

import com.boclips.kalturaclient.http.HttpClient;

public class CaptionAssetServeClient implements CaptionAssetServe {
    private final HttpClient client;

    public CaptionAssetServeClient(HttpClient client) {
        this.client = client;
    }

    @Override
    public String get(String sessionToken, String assetId) {
        return client.serveCaptionAsset(sessionToken, assetId);
    }
}
