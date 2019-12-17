package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.http.KalturaClientApiException;
import com.boclips.kalturaclient.http.ResponseObjectType;
import com.boclips.kalturaclient.media.resources.MediaEntryResource;
import com.boclips.kalturaclient.media.resources.MediaEntryStatusResource;
import com.boclips.kalturaclient.media.resources.MediaListResource;

import java.time.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MediaProcessor {

    public List<MediaEntry> process(MediaListResource mediaListResource) {

        if (!ResponseObjectType.isSuccessful(mediaListResource.objectType)) {
            throw new KalturaClientApiException(String.format("Error in Kaltura request: %s", mediaListResource.code));
        }

        return mediaListResource.objects
                .stream()
                .map(this::process)
                .collect(Collectors.toList());
    }

    private MediaEntry process(MediaEntryResource resource) {
        return MediaEntry.builder()
                .id(resource.getId())
                .referenceId(resource.getReferenceId())
                .downloadUrl(resource.getDownloadUrl())
                .duration(Duration.ofSeconds(resource.getDuration()))
                .status(MediaEntryStatus.from(MediaEntryStatusResource.fromInteger(resource.status)))
                .conversionProfileId(resource.getConversionProfileId())
                .createdAt(ZonedDateTime.ofInstant(Instant.ofEpochSecond(resource.getCreatedAt()), ZoneOffset.UTC))
                .flavorParamsIds(resource.getFlavorParamsIds() != null ? Arrays.asList(resource.getFlavorParamsIds().split(",")) : Collections.emptyList())
                .tags(resource.getTags() != null ? Arrays.asList(resource.getTags().split(",")) : Collections.emptyList())
                .playCount(resource.getPlays())
                .name(resource.name)
                .width(resource.width)
                .height(resource.height)
                .build();
    }
}
