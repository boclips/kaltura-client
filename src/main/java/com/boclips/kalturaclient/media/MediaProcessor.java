package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.media.resources.MediaEntryResource;
import com.boclips.kalturaclient.media.resources.MediaListResource;
import com.boclips.kalturaclient.media.streams.StreamUrlProducer;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class MediaProcessor {
    private StreamUrlProducer streamUrlProducer;

    public MediaProcessor(StreamUrlProducer streamUrlProducer) {
        this.streamUrlProducer = streamUrlProducer;
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
                .duration(Duration.ofSeconds(mediaEntryResource.getDuration()))
                .streams(streamUrlProducer.convert(mediaEntryResource))
                .thumbnailUrl(mediaEntryResource.getThumbnailUrl())
                .build();
    }
}
