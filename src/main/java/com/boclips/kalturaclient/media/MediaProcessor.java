package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.http.KalturaClientApiException;
import com.boclips.kalturaclient.http.ResponseObjectType;
import com.boclips.kalturaclient.media.resources.MediaEntryResource;
import com.boclips.kalturaclient.media.resources.MediaEntryStatusResource;
import com.boclips.kalturaclient.media.resources.MediaListResource;

import java.time.Duration;
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

    private MediaEntry process(MediaEntryResource mediaEntryResource) {
        return MediaEntry.builder()
                .id(mediaEntryResource.getId())
                .referenceId(mediaEntryResource.getReferenceId())
                .downloadUrl(mediaEntryResource.getDownloadUrl())
                .duration(Duration.ofSeconds(mediaEntryResource.getDuration()))
                .status(MediaEntryStatus.from(MediaEntryStatusResource.fromInteger(mediaEntryResource.status)))
                .build();
    }
}
