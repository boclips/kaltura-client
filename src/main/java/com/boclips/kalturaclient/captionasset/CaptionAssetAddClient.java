package com.boclips.kalturaclient.captionasset;

import com.boclips.kalturaclient.captionasset.resources.CaptionAssetResource;
import com.boclips.kalturaclient.http.HttpClient;

import java.util.HashMap;
import java.util.Map;

public class CaptionAssetAddClient implements CaptionAssetAdd {

    private final HttpClient client;

    public CaptionAssetAddClient(HttpClient client) {
        this.client = client;
    }

    @Override
    public CaptionAsset post(String entryId, CaptionAsset captionAsset) {
        Map<String, Object> params = new HashMap<>();
        params.put("entryId", entryId);
        params.put("captionAsset[format]", captionAsset.getFileType().getValue());
        params.put("captionAsset[language]", captionAsset.getLanguage());
        params.put("captionAsset[label]", captionAsset.getLabel());
        return client.post("/service/caption_captionasset/action/add", params, CaptionAssetResource.class)
                .toAsset();
    }
}
