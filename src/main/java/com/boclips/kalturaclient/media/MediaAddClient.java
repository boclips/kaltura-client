package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.http.HttpClient;

import java.util.HashMap;
import java.util.Map;

public class MediaAddClient implements MediaAdd {
    private final HttpClient client;

    public MediaAddClient(HttpClient client) {
        this.client = client;
    }

    @Override
    public void add(String sessionToken, String referenceId) {
        Map<String, Object> params = new HashMap<>();
        params.put("entry[mediaType]", 1);
        params.put("entry[objectType]", "KalturaMediaEntry");
        params.put("entry[referenceId]", referenceId);
        client.post("/service/media/action/add", params, String.class);
    }
}
