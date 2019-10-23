package com.boclips.kalturaclient.flavorAsset;

import com.boclips.kalturaclient.flavorAsset.resources.FlavorAssetListResource;
import com.boclips.kalturaclient.flavorAsset.resources.FlavorAssetResource;

import java.util.List;
import java.util.stream.Collectors;

public class FlavorAssetProcessor {

    public List<FlavorAsset> processFlavorAssetListResource(FlavorAssetListResource listResource) {
        return listResource.objects.stream().map(this::processFlavorAssetResource).collect(Collectors.toList());
    }

    private FlavorAsset processFlavorAssetResource(FlavorAssetResource assetResource) {
        return FlavorAsset.builder()
                .id(assetResource.getId())
                .entryId(assetResource.getEntryId())
                .flavorParamsId(assetResource.getFlavorParamsId())
                .size(assetResource.getSize())
                .isOriginal(assetResource.getIsOriginal())
                .build();
    }
}
