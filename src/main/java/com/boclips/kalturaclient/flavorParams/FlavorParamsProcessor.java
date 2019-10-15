package com.boclips.kalturaclient.flavorParams;

import com.boclips.kalturaclient.flavorParams.resources.FlavorParamsResource;
import com.boclips.kalturaclient.flavorParams.resources.FlavorParamsListResource;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FlavorParamsProcessor {
    public List<FlavorParams> process(FlavorParamsListResource resourceList) {
        return resourceList
                .getObjects()
                .stream()
                .map(this::processFlavorParamResource)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public FlavorParams processFlavorParamResource(FlavorParamsResource resource) {
        if (!resource.getObjectType().equals("KalturaFlavorParams")) {
            return null;
        }

        if ((resource.getHeight() == 0 && resource.getWidth() == 0) || resource.getVideoBitrate() == 0) {
            return null;
        }

        return FlavorParams.builder().id(resource.getId())
                .height(resource.getHeight())
                .width(resource.getWidth())
                .quality(getQuality(resource))
                .build();
    }

    private Quality getQuality(FlavorParamsResource resource) {
        final Integer height = resource.getHeight();
        final Integer bitrate = resource.getVideoBitrate();
        final boolean autoHeight = height == 0;

        if ((height >= 720 || autoHeight) && bitrate >= 2500) {
            return Quality.HIGH;
        }

        if ((height >= 500 || autoHeight) && bitrate >= 900) {
            return Quality.MEDIUM;
        }

        return Quality.LOW;
    }
}
