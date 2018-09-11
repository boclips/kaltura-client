package com.boclips.kalturaclient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestKalturaClient implements KalturaClient {

    private final Map<String, MediaEntry> mediaEntryByReferenceId = new HashMap<>();

    @Override
    public List<MediaEntry> mediaEntriesByReferenceIds(String... referenceIds) {
        return Arrays.stream(referenceIds)
                .map(mediaEntryByReferenceId::get)
                .collect(Collectors.toList());
    }

    public void addMediaEntry(MediaEntry mediaEntry) {
        mediaEntryByReferenceId.put(mediaEntry.getReferenceId(), mediaEntry);
    }
}
