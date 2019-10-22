package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.http.KalturaRestClient;
import com.boclips.kalturaclient.http.RequestFilters;
import com.boclips.kalturaclient.media.resources.MediaListResource;
import net.jodah.failsafe.RetryPolicy;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaListClient implements MediaList {
    private final KalturaRestClient client;
    private final MediaProcessor processor;

    public MediaListClient(KalturaRestClient client) {
        this.client = client;
        this.processor = new MediaProcessor();
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
