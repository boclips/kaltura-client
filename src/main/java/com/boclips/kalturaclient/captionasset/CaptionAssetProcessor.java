package com.boclips.kalturaclient.captionasset;

import com.boclips.kalturaclient.captionasset.resources.CaptionAssetListResource;
import com.boclips.kalturaclient.captionasset.resources.CaptionAssetResource;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

public class CaptionAssetProcessor {
    public List<CaptionAsset> processCaptionAssetListResource(CaptionAssetListResource resource) {
        return resource.objects.stream()
                .filter(Objects::nonNull)
                .map(CaptionAssetResource::toAsset)
                .collect(toList());
    }
}
