package com.boclips.kalturaclient;

import com.boclips.kalturaclient.media.MediaEntry;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public class TestKalturaClient implements KalturaClient {

    private final Map<String, List<MediaEntry>> mediaEntryByReferenceId = new HashMap<>();

    @Override
    public Map<String, List<MediaEntry>> getMediaEntriesByReferenceIds(Collection<String> referenceIds) {
        return referenceIds.stream()
                .filter(mediaEntryByReferenceId::containsKey)
                .collect(toMap(referenceId -> referenceId, mediaEntryByReferenceId::get));
    }

    @Override
    public List<MediaEntry> getMediaEntriesByReferenceId(String referenceId) {
        return Optional.ofNullable(mediaEntryByReferenceId.get(referenceId)).orElse(Collections.emptyList());
    }

    @Override
    public void deleteMediaEntriesByReferenceId(String referenceId) {
        mediaEntryByReferenceId.remove(referenceId);
    }

    @Override
    public void createMediaEntry(String referenceId) {
        addMediaEntry(MediaEntry.builder()
                .referenceId(referenceId)
                .build()
        );
    }

    public void addMediaEntry(MediaEntry mediaEntry) {
        mediaEntryByReferenceId
                .computeIfAbsent(mediaEntry.getReferenceId(), (String key) -> new ArrayList<>())
                .add(mediaEntry);
    }
}
