package com.boclips.kalturaclient.client;

import com.boclips.kalturaclient.KalturaClient;
import com.boclips.kalturaclient.KalturaClientConfig;
import com.boclips.kalturaclient.MediaEntry;
import com.boclips.kalturaclient.client.http.KalturaApiV3Client;
import com.boclips.kalturaclient.client.http.MediaEntryResource;
import com.boclips.kalturaclient.session.SessionGenerator;
import com.boclips.kalturaclient.streams.StreamUrlProducer;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpKalturaClient implements KalturaClient {
    private KalturaClientConfig config;
    private SessionGenerator sessionGenerator;
    private KalturaApiV3Client kalturaApiV3Client;

    public HttpKalturaClient(KalturaClientConfig config, SessionGenerator sessionGenerator) {
        this.config = config;
        this.sessionGenerator = sessionGenerator;
        this.kalturaApiV3Client = new KalturaApiV3Client(config.getBaseUrl());
    }

    @Override
    public Map<String, MediaEntry> mediaEntriesByReferenceIds(String... referenceIds) {
        List<MediaEntryResource> mediaEntryResources = kalturaApiV3Client
                .getMediaActionList(this.sessionGenerator.get().getToken(), Arrays.asList(referenceIds));

        StreamUrlProducer streamUrlProducer = new StreamUrlProducer(config);

        return mediaEntryResources.stream().map(mediaEntryResource -> MediaEntry.builder()
                .id(mediaEntryResource.getId())
                .referenceId(mediaEntryResource.getReferenceId())
                .duration(Duration.ofSeconds(mediaEntryResource.getDuration()))
                .streams(streamUrlProducer.convert(mediaEntryResource))
                .thumbnailUrl(mediaEntryResource.getThumbnailUrl())
                .build()).collect(Collectors.toMap(MediaEntry::getReferenceId, mediaEntry -> mediaEntry));
    }
}
