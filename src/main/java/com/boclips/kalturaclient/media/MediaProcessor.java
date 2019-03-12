package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.media.resources.MediaEntryResource;
import com.boclips.kalturaclient.media.resources.MediaEntryStatusResource;
import com.boclips.kalturaclient.media.resources.MediaListResource;
import com.boclips.kalturaclient.media.streams.StreamUrlProducer;
import com.boclips.kalturaclient.media.thumbnails.ThumbnailUrlProducer;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class MediaProcessor {
    private final ThumbnailUrlProducer thumbnailUrlProducer;
    private final StreamUrlProducer streamUrlProducer;

    public MediaProcessor(
            StreamUrlProducer streamUrlProducer,
            ThumbnailUrlProducer thumbnailUrlProducer) {
        this.streamUrlProducer = streamUrlProducer;
        this.thumbnailUrlProducer = thumbnailUrlProducer;
    }

    public List<MediaEntry> process(MediaListResource mediaListResource) {
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
                .thumbnailUrl(thumbnailUrlProducer.convert(mediaEntryResource))
                .status(MediaEntryStatus.from(MediaEntryStatusResource.fromInteger(mediaEntryResource.status)))
                .build();
    }
}
