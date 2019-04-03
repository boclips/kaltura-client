package com.boclips.kalturaclient.captionasset;

import com.boclips.kalturaclient.http.HttpClient;

import static java.util.Collections.singletonMap;

public class CaptionAssetServeClient implements CaptionAssetServe {
    private final HttpClient client;

    public CaptionAssetServeClient(HttpClient client) {
        this.client = client;
    }

    @Override
    public String get(String assetId) {
        return client.get(
                "/caption_captionasset/action/serve",
                singletonMap("captionAssetId", assetId),
                String.class
        );
    }
}
