package com.boclips.kalturaclient.client;

import com.boclips.kalturaclient.KalturaClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

public class TestKalturaClient implements KalturaClient {

    private final Map<String, MediaEntry> mediaEntryByReferenceId = new HashMap<>();

    @Override
    public Map<String, MediaEntry> mediaEntriesByReferenceIds(String... referenceIds) {
        return Arrays.stream(referenceIds)
                .filter(mediaEntryByReferenceId::containsKey)
                .collect(toMap(referenceId -> referenceId, mediaEntryByReferenceId::get));
    }

    @Override
    public Optional<MediaEntry> mediaEntryByReferenceId(String referenceId) {
        return Optional.ofNullable(mediaEntryByReferenceId.get(referenceId));
    }

    public void addMediaEntry(MediaEntry mediaEntry) {
        mediaEntryByReferenceId.put(mediaEntry.getReferenceId(), mediaEntry);
    }
}
