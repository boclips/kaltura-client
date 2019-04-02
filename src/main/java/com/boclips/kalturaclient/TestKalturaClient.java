package com.boclips.kalturaclient;

import com.boclips.kalturaclient.captionasset.CaptionAsset;
import com.boclips.kalturaclient.media.MediaEntry;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public class TestKalturaClient implements KalturaClient {

    private final Map<String, List<MediaEntry>> mediaEntryByReferenceId = new HashMap<>();

    private final Map<String, List<CaptionAsset>> captionAssetsByReferenceId = new HashMap<>();

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

    @Override
    public void createCaptionsFile(String referenceId, CaptionAsset captionAsset) {
        CaptionAsset copyWithId = captionAsset
                .toBuilder()
                .id(UUID.randomUUID().toString())
                .build();
        captionAssetsByReferenceId.computeIfAbsent(referenceId, (refId) -> new ArrayList<>())
                .add(copyWithId);
    }

    @Override
    public List<CaptionAsset> getCaptionFilesByReferenceId(String referenceId) {
        return captionAssetsByReferenceId.get(referenceId);
    }

    public void addMediaEntry(MediaEntry mediaEntry) {
        mediaEntryByReferenceId
                .computeIfAbsent(mediaEntry.getReferenceId(), (String key) -> new ArrayList<>())
                .add(mediaEntry);
    }

    public void clear() {
        mediaEntryByReferenceId.clear();
    }
}
