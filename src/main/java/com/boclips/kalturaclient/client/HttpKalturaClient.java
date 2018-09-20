package com.boclips.kalturaclient.client;

import com.boclips.kalturaclient.KalturaClient;
import com.boclips.kalturaclient.KalturaClientConfig;
import com.boclips.kalturaclient.client.media.MediaEntryResource;
import com.boclips.kalturaclient.client.media.MediaListClient;
import com.boclips.kalturaclient.client.media.RequestFilters;
import com.boclips.kalturaclient.session.SessionGenerator;
import com.boclips.kalturaclient.streams.StreamUrlProducer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpKalturaClient implements KalturaClient {
    private KalturaClientConfig config;
    private SessionGenerator sessionGenerator;
    private MediaListClient mediaListClient;

    public HttpKalturaClient(KalturaClientConfig config, SessionGenerator sessionGenerator) {
        this.config = config;
        this.sessionGenerator = sessionGenerator;
        this.mediaListClient = new MediaListClient(config.getBaseUrl());
    }

    @Override
    public Map<String, MediaEntry> mediaEntriesByReferenceIds(String... referenceIds) {
        List<MediaEntryResource> mediaEntryResources = mediaListClient
                .getMediaActionList(this.sessionGenerator.get().getToken(), createFilters(referenceIds));

        StreamUrlProducer streamUrlProducer = new StreamUrlProducer(config);

        return mediaEntryResources.stream().map(mediaEntryResource -> MediaEntry.builder()
                .id(mediaEntryResource.getId())
                .referenceId(mediaEntryResource.getReferenceId())
                .duration(Duration.ofSeconds(mediaEntryResource.getDuration()))
                .streams(streamUrlProducer.convert(mediaEntryResource))
                .thumbnailUrl(mediaEntryResource.getThumbnailUrl())
                .build()).collect(Collectors.toMap(MediaEntry::getReferenceId, mediaEntry -> mediaEntry));
    }

    @Override
    public Optional<MediaEntry> mediaEntryByReferenceId(String referenceId) {
        return Optional.ofNullable(mediaEntriesByReferenceIds(referenceId).get(referenceId));
    }

    private RequestFilters createFilters(String[] referenceIds) {
        return new RequestFilters()
                .add("filter[referenceIdIn]", String.join(",", referenceIds));
    }
}
