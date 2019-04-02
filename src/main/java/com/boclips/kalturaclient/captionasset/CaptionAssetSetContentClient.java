package com.boclips.kalturaclient.captionasset;

import com.boclips.kalturaclient.KalturaClientConfig;
import com.boclips.kalturaclient.http.HttpClient;

public class CaptionAssetSetContentClient implements CaptionAssetSetContent {
    private final HttpClient client;

    public CaptionAssetSetContentClient(KalturaClientConfig config) {
        this.client = new HttpClient(config.getBaseUrl());
    }

    @Override
    public void post(String sessionToken, String captionAssetId, String content) {
        client.setCaptionAssetContent(sessionToken, captionAssetId, content);
    }
}
