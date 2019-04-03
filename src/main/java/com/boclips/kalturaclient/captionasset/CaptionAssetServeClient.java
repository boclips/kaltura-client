package com.boclips.kalturaclient.captionasset;

import com.boclips.kalturaclient.http.HttpClient;

import static java.util.Collections.singletonMap;

public class CaptionAssetServeClient implements CaptionAssetServe {
    private final HttpClient client;

    public CaptionAssetServeClient(HttpClient client) {
        this.client = client;
    }

    @Override
    public String get(String sessionToken, String assetId) {
        return client.get(
                "/service/caption_captionasset/action/serve",
                singletonMap("captionAssetId", assetId),
                String.class
        );
    }
}
