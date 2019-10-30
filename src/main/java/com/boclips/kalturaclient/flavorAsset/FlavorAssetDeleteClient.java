package com.boclips.kalturaclient.flavorAsset;

import com.boclips.kalturaclient.http.KalturaClientApiException;
import com.boclips.kalturaclient.http.KalturaRestClient;

import static java.util.Collections.singletonMap;

public class FlavorAssetDeleteClient implements FlavorAssetDelete {

    private final KalturaRestClient client;

    public FlavorAssetDeleteClient(KalturaRestClient client) {

        this.client = client;
    }

    @Override
    public void deleteByAssetId(String assetId) {
        String response = client.post("/flavorasset/action/delete", singletonMap("id", assetId), String.class);

        if (response.contains("KalturaAPIException")) {
            throw new KalturaClientApiException(
                    String.format("Asset %s was not deleted, API returned %s",
                            assetId,
                            response)
            );
        }
    }
}
