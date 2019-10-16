package com.boclips.kalturaclient.captionasset;

import com.boclips.kalturaclient.http.KalturaRestClient;

import static java.util.Collections.singletonMap;

public class CaptionAssetDelete {

    private final KalturaRestClient kalturaRestClient;

    public CaptionAssetDelete(KalturaRestClient kalturaRestClient) {
        this.kalturaRestClient = kalturaRestClient;
    }

    public void post(String assetId) {
        kalturaRestClient.post("/caption_captionasset/action/delete", singletonMap("captionAssetId", assetId), String.class);
    }
}
