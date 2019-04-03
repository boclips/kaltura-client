package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.http.HttpClient;
import com.boclips.kalturaclient.http.KalturaClientApiException;

import static java.util.Collections.singletonMap;

public class MediaDeleteClient implements MediaDelete {
    private final HttpClient client;

    public MediaDeleteClient(HttpClient client) {
        this.client = client;
    }

    @Override
    public void deleteByEntityId(String session, String entityId) {
        String response = client.post("/media/action/delete", singletonMap("entryId", entityId), String.class);

        if (response.contains("KalturaAPIException")) {
            throw new KalturaClientApiException(
                    String.format("Media Entry %s was not deleted, API returned %s",
                            entityId,
                            response)
            );
        }
    }
}
