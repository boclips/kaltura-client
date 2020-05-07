package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.http.KalturaRestClient;
import com.boclips.kalturaclient.media.resources.MediaEntryResource;

import java.util.HashMap;
import java.util.Map;

public class MediaAddClient implements MediaAdd {
    private final KalturaRestClient client;
    private final MediaProcessor processor;

    public MediaAddClient(KalturaRestClient client) {
        this.client = client;
        this.processor = new MediaProcessor();
    }

    @Override
    public MediaEntry add(String referenceId) {
        Map<String, Object> params = new HashMap<>();
        params.put("entry[mediaType]", 1);
        params.put("entry[objectType]", "KalturaMediaEntry");
        params.put("entry[referenceId]", referenceId);
        MediaEntryResource resource = client.post("/media/action/add", params, MediaEntryResource.class);
        return this.processor.process(resource);
    }
}
