package com.boclips.kalturaclient.thumbnailAsset;

import com.boclips.kalturaclient.http.KalturaRestClient;

import java.util.HashMap;
import java.util.Map;

public class SetThumbnailAsDefaultClient implements SetThumbnailAsDefault {

    private final KalturaRestClient client;

    public SetThumbnailAsDefaultClient(KalturaRestClient restClient) {
        this.client = restClient;
    }

    @Override
    public void setAsDefaultByThumbAssetId(String thumbAssetId) {
        Map<String, Object> bodyParams = new HashMap<>();
        bodyParams.put("thumbAssetId", thumbAssetId);

        this.client.post("/thumbasset/action/setAsDefault", new HashMap<>(), bodyParams, Object.class);
    }
}
