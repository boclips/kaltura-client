package com.boclips.kalturaclient.captionasset;

import com.boclips.kalturaclient.http.KalturaRestClient;

import java.net.URI;

import static java.util.Collections.singletonMap;

public class CaptionAssetGetUrl {

    private final KalturaRestClient kalturaRestClient;

    public CaptionAssetGetUrl(KalturaRestClient kalturaRestClient) {
        this.kalturaRestClient = kalturaRestClient;
    }

    public URI post(String assetId) {
        return kalturaRestClient.post("/caption_captionasset/action/getUrl", singletonMap("id", assetId), URI.class);
    }
}
