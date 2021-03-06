package com.boclips.kalturaclient.baseentry;

import com.boclips.kalturaclient.http.KalturaRestClient;

import java.util.HashMap;
import java.util.Map;

public class BaseEntryUpdateClient implements BaseEntryUpdate {
    private final KalturaRestClient client;

    public BaseEntryUpdateClient(KalturaRestClient client) {
        this.client = client;
    }

    @Override
    public void post(String entryId, BaseEntry baseEntry) {
        Map<String, Object> params = new HashMap<>();
        params.put("entryId", entryId);
        if (baseEntry.isTagged()) {
            params.put("baseEntry[tags]", String.join(", ", baseEntry.getTags()));
        }
        if (baseEntry.hasCategories()) {
            params.put("baseEntry[categories]", String.join(", ", baseEntry.getCategories()));
        }

        client.post("/baseentry/action/update", params, String.class);
    }
}
