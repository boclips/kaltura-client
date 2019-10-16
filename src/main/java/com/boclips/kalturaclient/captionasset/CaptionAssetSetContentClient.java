package com.boclips.kalturaclient.captionasset;

import com.boclips.kalturaclient.captionasset.resources.CaptionAssetResource;
import com.boclips.kalturaclient.http.KalturaRestClient;

import java.util.HashMap;
import java.util.Map;

public class CaptionAssetSetContentClient implements CaptionAssetSetContent {
    private final KalturaRestClient client;

    public CaptionAssetSetContentClient(KalturaRestClient client) {
        this.client = client;
    }

    @Override
    public CaptionAsset post(String captionAssetId, String content) {
        Map<String, Object> query = new HashMap<>();
        query.put("id", captionAssetId);
        query.put("contentResource[objectType]", "KalturaStringResource");

        Map<String, Object> bodyParams = new HashMap<>();
        bodyParams.put("contentResource[content]", content);

        return client.post("/caption_captionasset/action/setContent", query, bodyParams, CaptionAssetResource.class).toAsset();
    }
}
