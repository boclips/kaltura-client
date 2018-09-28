package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.KalturaClientConfig;
import com.boclips.kalturaclient.http.HttpClient;

public class MediaAddClient implements MediaAdd {
    private final HttpClient client;

    public MediaAddClient(KalturaClientConfig config) {
        this.client = new HttpClient(config.getBaseUrl());
    }

    @Override
    public void add(String sessionToken, String referenceId) {
        client.addMediaEntry(sessionToken, referenceId);
    }
}
