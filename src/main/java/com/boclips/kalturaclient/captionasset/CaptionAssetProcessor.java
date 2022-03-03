package com.boclips.kalturaclient.captionasset;

import com.boclips.kalturaclient.captionasset.resources.CaptionAssetListResource;
import com.boclips.kalturaclient.captionasset.resources.CaptionAssetResource;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Slf4j
public class CaptionAssetProcessor {
    public List<CaptionAsset> processCaptionAssetListResource(CaptionAssetListResource resource) {
        if(resource == null) {
            log.warn("CaptionAsset was null - returning empty captions list");
            return List.of();
        }

        if(resource.objects == null) {
            log.warn("CaptionAsset's objects was null - returning empty captions list");
            return List.of();
        }

        return resource.objects.stream()
                .filter(Objects::nonNull)
                .map(CaptionAssetResource::toAsset)
                .collect(toList());
    }
}
