package com.boclips.kalturaclient;

import com.boclips.kalturaclient.http.RequestFilters;
import com.boclips.kalturaclient.media.MediaEntry;
import com.boclips.kalturaclient.media.MediaList;
import com.boclips.kalturaclient.media.MediaListClient;
import com.boclips.kalturaclient.session.SessionGenerator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

public class KalturaClientV3 implements KalturaClient {
    private SessionGenerator sessionGenerator;
    private MediaList mediaList;

    public KalturaClientV3(KalturaClientConfig config, SessionGenerator sessionGenerator) {
        this.sessionGenerator = sessionGenerator;
        this.mediaList = new MediaListClient(config);
    }

    @Override
    public Map<String, MediaEntry> mediaEntriesByReferenceIds(String... referenceIds) {
        List<MediaEntry> mediaEntries = mediaList.get(this.sessionGenerator, createFilters(referenceIds));
        return toMapByReferenceIdIgnoringDuplicates(mediaEntries);
    }

    static Map<String, MediaEntry> toMapByReferenceIdIgnoringDuplicates(List<MediaEntry> mediaEntries) {
        return mediaEntries
                .stream()
                .collect(Collectors.toMap(MediaEntry::getReferenceId, identity(), (entry1, entry2) -> entry1));
    }

    @Override
    public Optional<MediaEntry> mediaEntryByReferenceId(String referenceId) {
        return Optional.ofNullable(mediaEntriesByReferenceIds(referenceId).get(referenceId));
    }

    private RequestFilters createFilters(String[] referenceIds) {
        return new RequestFilters()
                .add("filter[referenceIdIn]", String.join(",", referenceIds));
    }
}
