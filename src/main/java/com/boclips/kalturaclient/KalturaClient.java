package com.boclips.kalturaclient;

import com.boclips.kalturaclient.media.MediaEntry;
import com.boclips.kalturaclient.session.RestSessionGenerator;
import com.boclips.kalturaclient.session.SessionRetriever;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface KalturaClient {
    static KalturaClient create(KalturaClientConfig config) {
        return new KalturaClientV3(config, new RestSessionGenerator(new SessionRetriever(config), config.getSessionTtl()));
    }

    Map<String, MediaEntry> getMediaEntriesByReferenceIds(Collection<String> referenceIds);

    Optional<MediaEntry> getMediaEntryByReferenceId(String referenceIds);

}
