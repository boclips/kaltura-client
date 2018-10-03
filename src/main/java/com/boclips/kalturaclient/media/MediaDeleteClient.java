package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.KalturaClientConfig;
import com.boclips.kalturaclient.http.HttpClient;

public class MediaDeleteClient implements MediaDelete {
    private final HttpClient client;

    public MediaDeleteClient(KalturaClientConfig config) {
        client = new HttpClient(config.getBaseUrl());
    }

    @Override
    public void deleteByEntityId(String session, String entityId) {
        client.deleteMediaEntryByEntityId(session, entityId);
    }
}
