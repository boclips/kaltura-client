package com.boclips.kalturaclient.baseentry;

import com.boclips.kalturaclient.baseentry.resources.BaseEntryResource;
import com.boclips.kalturaclient.http.KalturaRestClient;

import java.util.HashMap;
import java.util.Map;

public class BaseEntryGetClient implements BaseEntryGet {
    private final KalturaRestClient client;

    public BaseEntryGetClient(KalturaRestClient client) {
        this.client = client;
    }

    @Override
    public BaseEntry get(String entryId) {
        Map<String, Object> params = new HashMap<>();
        params.put("entryId", entryId);
        return client.get("/baseentry/action/get", params, BaseEntryResource.class)
                .toBaseEntry();
    }
}
