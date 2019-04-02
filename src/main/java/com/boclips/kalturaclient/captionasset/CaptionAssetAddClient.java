package com.boclips.kalturaclient.captionasset;

import com.boclips.kalturaclient.KalturaClientConfig;
import com.boclips.kalturaclient.captionasset.resources.CaptionAssetResource;
import com.boclips.kalturaclient.http.HttpClient;

public class CaptionAssetAddClient implements CaptionAssetAdd {

    private final HttpClient client;

    public CaptionAssetAddClient(KalturaClientConfig config) {
        this.client = new HttpClient(config.getBaseUrl());
    }

    @Override
    public CaptionAsset post(String sessionToken, String entryId, CaptionAsset captionAsset) {
        return client.addCaptionAsset(sessionToken, entryId, captionAsset)
                .toAsset();
    }
}
