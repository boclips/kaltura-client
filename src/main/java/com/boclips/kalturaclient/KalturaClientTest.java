package com.boclips.kalturaclient;

import com.boclips.kalturaclient.media.MediaEntry;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public class KalturaClientTest implements KalturaClient {

    private final Map<String, MediaEntry> mediaEntryByReferenceId = new HashMap<>();

    @Override
    public Map<String, MediaEntry> getMediaEntriesByReferenceIds(Collection<String> referenceIds) {
        return referenceIds.stream()
                .filter(mediaEntryByReferenceId::containsKey)
                .collect(toMap(referenceId -> referenceId, mediaEntryByReferenceId::get));
    }

    @Override
    public Optional<MediaEntry> getMediaEntryByReferenceId(String referenceId) {
        return Optional.ofNullable(mediaEntryByReferenceId.get(referenceId));
    }

    public void addMediaEntry(MediaEntry mediaEntry) {
        mediaEntryByReferenceId.put(mediaEntry.getReferenceId(), mediaEntry);
    }
}
