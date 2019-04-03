package com.boclips.kalturaclient.baseentry;

import com.boclips.kalturaclient.baseentry.resources.BaseEntryResource;
import com.boclips.kalturaclient.http.HttpClient;

import java.util.HashMap;
import java.util.Map;

public class BaseEntryUpdateClient implements BaseEntryUpdate {
    private final HttpClient client;

    public BaseEntryUpdateClient(HttpClient client) {
        this.client = client;
    }

    @Override
    public void post(String entryId, BaseEntry baseEntry) {
        Map<String, Object> params = new HashMap<>();
        params.put("entryId", entryId);
        params.put("baseEntry[tags]", String.join(", ", baseEntry.getTags()));

        client.post("/baseentry/action/update", params, String.class);
    }
}
