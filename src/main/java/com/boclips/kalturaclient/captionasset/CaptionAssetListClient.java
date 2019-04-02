package com.boclips.kalturaclient.captionasset;

import com.boclips.kalturaclient.KalturaClientConfig;
import com.boclips.kalturaclient.captionasset.resources.CaptionAssetListItemResource;
import com.boclips.kalturaclient.captionasset.resources.CaptionAssetListResource;
import com.boclips.kalturaclient.http.HttpClient;
import com.boclips.kalturaclient.http.RequestFilters;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class CaptionAssetListClient implements CaptionAssetList {

    private final HttpClient client;

    public CaptionAssetListClient(KalturaClientConfig config) {
        this.client = new HttpClient(config.getBaseUrl());
    }

    @Override
    public List<CaptionAsset> get(String sessionToken, RequestFilters filters) {
        CaptionAssetListResource resources = client.listCaptionAssets(sessionToken, filters);

        return resources.objects
                .stream()
                .map(this::resourceToAsset)
                .collect(toList());
    }

    private CaptionAsset resourceToAsset(CaptionAssetListItemResource item) {
        return CaptionAsset.builder()
                .id(item.id)
                .label(item.label)
                .language(item.language)
                .fileType(CaptionFormat.fromValue(item.format))
                .build();
    }
}
