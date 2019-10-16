package com.boclips.kalturaclient.captionasset;

import com.boclips.kalturaclient.http.KalturaRestClient;

import static java.util.Collections.singletonMap;

public class CaptionAssetServeClient implements CaptionAssetServe {
    private final KalturaRestClient client;

    public CaptionAssetServeClient(KalturaRestClient client) {
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
