package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.http.KalturaRestClient;

import java.util.HashMap;
import java.util.Map;

public class MediaAddClient implements MediaAdd {
    private final KalturaRestClient client;

    public MediaAddClient(KalturaRestClient client) {
        this.client = client;
    }

    @Override
    public void add(String referenceId) {
        Map<String, Object> params = new HashMap<>();
        params.put("entry[mediaType]", 1);
        params.put("entry[objectType]", "KalturaMediaEntry");
        params.put("entry[referenceId]", referenceId);
        client.post("/media/action/add", params, String.class);
    }
}
