package com.boclips.kalturaclient;

import com.boclips.kalturaclient.session.RestSessionGenerator;
import com.boclips.kalturaclient.session.SessionRetriever;

import java.util.List;

public interface KalturaClient {

    static KalturaClient create(KalturaClientConfig config) {
        return new HttpKalturaClient(config, new RestSessionGenerator(new SessionRetriever(config), config.getSessionTtl()));
    }

    List<MediaEntry> mediaEntriesByReferenceIds(String... referenceIds);
}
