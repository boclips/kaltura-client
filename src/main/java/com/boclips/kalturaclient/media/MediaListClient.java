package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.KalturaClientConfig;
import com.boclips.kalturaclient.http.HttpClient;
import com.boclips.kalturaclient.http.RequestFilters;
import com.boclips.kalturaclient.media.resources.MediaListResource;
import com.boclips.kalturaclient.media.streams.StreamUrlProducer;

import java.util.List;

public class MediaListClient implements MediaList {
    private final HttpClient client;
    private final MediaProcessor processor;

    public MediaListClient(KalturaClientConfig config) {
        this.client = new HttpClient(config.getBaseUrl());
        this.processor = new MediaProcessor(new StreamUrlProducer(config));
    }

    @Override
    public List<MediaEntry> get(String sessionToken, RequestFilters filters) {
        MediaListResource mediaListResource = client.listMediaEntries(sessionToken, filters);
        return this.processor.process(mediaListResource);
    }

    @Override
    public Long count(String sessionToken, RequestFilters filters) {
        MediaListResource mediaListResource = client.listMediaEntries(sessionToken, filters);
        return mediaListResource.totalCount;
    }
}
