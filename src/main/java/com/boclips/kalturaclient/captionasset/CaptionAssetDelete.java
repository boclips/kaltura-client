package com.boclips.kalturaclient.captionasset;

import com.boclips.kalturaclient.http.HttpClient;

import static java.util.Collections.singletonMap;

public class CaptionAssetDelete {

    private final HttpClient httpClient;

    public CaptionAssetDelete(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void post(String assetId) {
        httpClient.post("/caption_captionasset/action/delete", singletonMap("captionAssetId", assetId), String.class);
    }
}
