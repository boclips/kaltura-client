package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.http.KalturaClientApiException;
import com.boclips.kalturaclient.http.ResponseObjectType;
import com.boclips.kalturaclient.media.links.LinkBuilder;
import com.boclips.kalturaclient.media.resources.MediaEntryResource;
import com.boclips.kalturaclient.media.resources.MediaEntryStatusResource;
import com.boclips.kalturaclient.media.resources.MediaListResource;
import com.boclips.kalturaclient.media.streams.StreamUrlProducer;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class MediaProcessor {
    private final LinkBuilder linkBuilder;
    private final StreamUrlProducer streamUrlProducer;

    public MediaProcessor(
            StreamUrlProducer streamUrlProducer,
            LinkBuilder linkBuilder) {
        this.streamUrlProducer = streamUrlProducer;
        this.linkBuilder = linkBuilder;
    }

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
                .streams(streamUrlProducer.convert(mediaEntryResource))
                .thumbnailUrl(linkBuilder.getThumbnailUrl(mediaEntryResource))
                .videoPreviewUrl(linkBuilder.getVideoPreviewUrl(mediaEntryResource))
                .status(MediaEntryStatus.from(MediaEntryStatusResource.fromInteger(mediaEntryResource.status)))
                .build();
    }
}
