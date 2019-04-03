package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.KalturaClientConfig;
import com.boclips.kalturaclient.http.HttpClient;
import com.boclips.kalturaclient.http.RequestFilters;
import com.boclips.kalturaclient.http.ResponseObjectType;
import com.boclips.kalturaclient.media.resources.MediaListResource;
import com.boclips.kalturaclient.media.streams.StreamUrlProducer;
import com.boclips.kalturaclient.media.thumbnails.ThumbnailUrlProducer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaListClient implements MediaList {
    private final HttpClient client;
    private final MediaProcessor processor;

    public MediaListClient(HttpClient client, KalturaClientConfig config) {
        this.client = client;
        this.processor = new MediaProcessor(
                new StreamUrlProducer(config),
                new ThumbnailUrlProducer(config)
        );
    }

    @Override
    public List<MediaEntry> get(RequestFilters filters) {
        MediaListResource mediaListResource = listMediaEntries(filters);
        return this.processor.process(mediaListResource);
    }

    @Override
    public Long count(RequestFilters filters) {
        MediaListResource mediaListResource = listMediaEntries(filters);
        return mediaListResource.totalCount;
    }

    private MediaListResource listMediaEntries(RequestFilters filters) {
        Map<String, Object> params = new HashMap<>(filters.toMap());
        params.put("filter[statusIn]", "-2,-1,0,1,2,4,5,6,7");
        return client.get("/media/action/list", params, MediaListResource.class);
    }
}
