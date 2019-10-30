package com.boclips.kalturaclient.flavorAsset;

import com.boclips.kalturaclient.flavorAsset.resources.FlavorAssetListResource;
import com.boclips.kalturaclient.flavorAsset.resources.FlavorAssetResource;

import java.util.List;
import java.util.stream.Collectors;

public class FlavorAssetProcessor {

    public List<Asset> processFlavorAssetListResource(FlavorAssetListResource listResource) {
        return listResource.objects.stream().map(this::processFlavorAssetResource).collect(Collectors.toList());
    }

    private Asset processFlavorAssetResource(FlavorAssetResource assetResource) {
        return Asset.builder()
                .id(assetResource.getId())
                .entryId(assetResource.getEntryId())
                .flavorParamsId(assetResource.getFlavorParamsId())
                .size(assetResource.getSize())
                .width(assetResource.getWidth())
                .height(assetResource.getHeight())
                .isOriginal(assetResource.getIsOriginal())
                .build();
    }
}
