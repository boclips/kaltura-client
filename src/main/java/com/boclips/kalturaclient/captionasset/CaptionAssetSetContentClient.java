package com.boclips.kalturaclient.captionasset;

import com.boclips.kalturaclient.http.HttpClient;

public class CaptionAssetSetContentClient implements CaptionAssetSetContent {
    private final HttpClient client;

    public CaptionAssetSetContentClient(HttpClient client) {
        this.client = client;
    }

    @Override
    public void post(String sessionToken, String captionAssetId, String content) {
        client.setCaptionAssetContent(sessionToken, captionAssetId, content);
    }
}
