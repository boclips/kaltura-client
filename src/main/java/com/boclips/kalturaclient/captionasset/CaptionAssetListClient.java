package com.boclips.kalturaclient.captionasset;

import com.boclips.kalturaclient.captionasset.resources.CaptionAssetListResource;
import com.boclips.kalturaclient.http.KalturaRestClient;
import com.boclips.kalturaclient.http.RequestFilters;

import java.util.List;

public class CaptionAssetListClient implements CaptionAssetList {

    private final KalturaRestClient client;
    private final CaptionAssetProcessor processor;

    public CaptionAssetListClient(KalturaRestClient client) {
        this.client = client;
        this.processor = new CaptionAssetProcessor();
    }

    @Override
    public List<CaptionAsset> get(RequestFilters filters) {
        CaptionAssetListResource resources = client.get("/caption_captionasset/action/list", filters.toMap(), CaptionAssetListResource.class);

        return processor.processCaptionAssetListResource(resources);
    }
}
