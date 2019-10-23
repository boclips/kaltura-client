package com.boclips.kalturaclient.flavorAsset;

import com.boclips.kalturaclient.flavorAsset.resources.FlavorAssetListResource;
import com.boclips.kalturaclient.http.KalturaRestClient;
import com.boclips.kalturaclient.http.RequestFilters;

import java.util.List;

public class FlavorAssetListClient implements FlavorAssetList {

    private final KalturaRestClient client;
    private final FlavorAssetProcessor processor;

    public FlavorAssetListClient(KalturaRestClient client) {
        this.client = client;
        this.processor = new FlavorAssetProcessor();
    }

    @Override
    public List<FlavorAsset> list(RequestFilters filters) {
        FlavorAssetListResource listResource = this.client.get("/flavorasset/action/list", filters.toMap(), FlavorAssetListResource.class);

        return processor.processFlavorAssetListResource(listResource);
    }
}
