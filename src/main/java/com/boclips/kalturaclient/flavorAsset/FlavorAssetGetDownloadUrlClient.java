package com.boclips.kalturaclient.flavorAsset;

import com.boclips.kalturaclient.flavorAsset.resources.FlavorAssetListResource;
import com.boclips.kalturaclient.http.KalturaRestClient;
import com.boclips.kalturaclient.http.RequestFilters;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlavorAssetGetDownloadUrlClient implements FlavorAssetGetDownloadUrl {

    private final KalturaRestClient client;
    private final FlavorAssetProcessor processor;

    public FlavorAssetGetDownloadUrlClient(KalturaRestClient client) {
        this.client = client;
        this.processor = new FlavorAssetProcessor();
    }

    @Override
    public URI getDownloadUrl(String assetId) {
        Map map = new HashMap();
        map.put("id", assetId);
        URI uri = this.client.get("/flavorasset/action/getUrl", map, URI.class);

        return uri;
    }

}
