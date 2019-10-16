package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.http.KalturaRestClient;
import com.boclips.kalturaclient.http.KalturaClientApiException;

import static java.util.Collections.singletonMap;

public class MediaDeleteClient implements MediaDelete {
    private final KalturaRestClient client;

    public MediaDeleteClient(KalturaRestClient client) {
        this.client = client;
    }

    @Override
    public void deleteByEntryId(String entryId) {
        String response = client.post("/media/action/delete", singletonMap("entryId", entryId), String.class);

        if (response.contains("KalturaAPIException")) {
            throw new KalturaClientApiException(
                    String.format("Media Entry %s was not deleted, API returned %s",
                            entryId,
                            response)
            );
        }
    }
}
