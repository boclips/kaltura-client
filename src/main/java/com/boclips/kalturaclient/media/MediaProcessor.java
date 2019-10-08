package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.http.KalturaClientApiException;
import com.boclips.kalturaclient.http.ResponseObjectType;
import com.boclips.kalturaclient.media.resources.MediaEntryResource;
import com.boclips.kalturaclient.media.resources.MediaEntryStatusResource;
import com.boclips.kalturaclient.media.resources.MediaListResource;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
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
                .createdAt(LocalDateTime.ofEpochSecond(resource.createdAt, 0, ZoneOffset.UTC))
                .flavorParamsIds(Arrays.asList(resource.getFlavorParamsIds().split(",")))
                .tags(Arrays.asList(resource.tags.split(",")))
                .playCount(resource.getPlays())
                .build();
    }
}
