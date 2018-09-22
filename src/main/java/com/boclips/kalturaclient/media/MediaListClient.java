package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.http.HttpClient;
import com.boclips.kalturaclient.http.RequestFilters;
import com.boclips.kalturaclient.media.resources.MediaEntryResource;
import com.boclips.kalturaclient.media.resources.MediaListResource;
import com.boclips.kalturaclient.session.SessionGenerator;

import java.util.List;

public class MediaListClient implements MediaList {
    private final HttpClient client;

    public MediaListClient(String baseUrl) {
        this.client = new HttpClient(baseUrl);
    }

    @Override
    public List<MediaEntryResource> get(SessionGenerator sessionToken, RequestFilters filters) {
        MediaListResource mediaListResource = client.getMediaListResource(sessionToken.get().getToken(), filters);
        return mediaListResource.objects;
    }

    @Override
    public Long count(String sessionToken, RequestFilters filters) {
        MediaListResource mediaListResource = client.getMediaListResource(sessionToken, filters);
        return mediaListResource.totalCount;
    }
}
