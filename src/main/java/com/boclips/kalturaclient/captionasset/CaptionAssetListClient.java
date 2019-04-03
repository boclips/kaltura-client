package com.boclips.kalturaclient.captionasset;

import com.boclips.kalturaclient.captionasset.resources.CaptionAssetResource;
import com.boclips.kalturaclient.captionasset.resources.CaptionAssetListResource;
import com.boclips.kalturaclient.http.HttpClient;
import com.boclips.kalturaclient.http.RequestFilters;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class CaptionAssetListClient implements CaptionAssetList {

    private final HttpClient client;

    public CaptionAssetListClient(HttpClient client) {
        this.client = client;
    }

    @Override
    public List<CaptionAsset> get(RequestFilters filters) {
        CaptionAssetListResource resources = client.get("/caption_captionasset/action/list", filters.toMap(), CaptionAssetListResource.class);

        return resources.objects
                .stream()
                .map(CaptionAssetResource::toAsset)
                .collect(toList());
    }
}
