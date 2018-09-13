package com.boclips.kalturaclient;

import com.boclips.kalturaclient.client.HttpKalturaClient;
import com.boclips.kalturaclient.session.RestSessionGenerator;
import com.boclips.kalturaclient.session.SessionRetriever;

import java.util.Map;
import java.util.Optional;

public interface KalturaClient {
    static KalturaClient create(KalturaClientConfig config) {
        return new HttpKalturaClient(config, new RestSessionGenerator(new SessionRetriever(config), config.getSessionTtl()));
    }

    Map<String, MediaEntry> mediaEntriesByReferenceIds(String... referenceIds);
    Optional<MediaEntry> mediaEntryByReferenceId(String referenceIds);
}
