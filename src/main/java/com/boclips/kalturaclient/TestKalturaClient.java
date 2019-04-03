package com.boclips.kalturaclient;

import com.boclips.kalturaclient.captionasset.CaptionAsset;
import com.boclips.kalturaclient.media.MediaEntry;
import com.boclips.kalturaclient.media.MediaEntryStatus;
import com.boclips.kalturaclient.media.streams.StreamUrls;

import java.time.Duration;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public class TestKalturaClient implements KalturaClient {

    private final Map<String, List<MediaEntry>> mediaEntryByReferenceId = new HashMap<>();

    private final Map<String, List<CaptionAsset>> captionAssetsByReferenceId = new HashMap<>();
    private final Map<String, String> captionContentsByAssetId = new HashMap<>();

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
        String id = UUID.randomUUID().toString();
        addMediaEntry(MediaEntry.builder()
                .referenceId(referenceId)
                .id(id)
                .downloadUrl(downloadUrl(id))
                .duration(Duration.ofSeconds(92))
                .streams(streamUrl(id))
                .thumbnailUrl(thumbnailUrl(id))
                .status(MediaEntryStatus.NOT_READY)
                .build()
        );
    }

    @Override
    public void createCaptionsFile(String referenceId, CaptionAsset captionAsset, String content) {
        String assetId = UUID.randomUUID().toString();
        CaptionAsset copyWithId = captionAsset
                .toBuilder()
                .id(assetId)
                .build();
        captionAssetsByReferenceId.computeIfAbsent(referenceId, (refId) -> new ArrayList<>())
                .add(copyWithId);
        captionContentsByAssetId.put(assetId, content);
    }

    @Override
    public List<CaptionAsset> getCaptionFilesByReferenceId(String referenceId) {
        return captionAssetsByReferenceId.get(referenceId);
    }

    @Override
    public String getCaptionContentByAssetId(String assetId) {
        return captionContentsByAssetId.get(assetId);
    }

    public void addMediaEntry(MediaEntry mediaEntry) {
        mediaEntryByReferenceId
                .computeIfAbsent(mediaEntry.getReferenceId(), (String key) -> new ArrayList<>())
                .add(mediaEntry);
    }

    public void clear() {
        mediaEntryByReferenceId.clear();
    }

    private static String downloadUrl(String id) {
        return "https://cdnapisec.kaltura.com/p/" + id + ".mp4";
    }

    private static StreamUrls streamUrl(String id) {
        return new StreamUrls("https://stream.com/s/" + id + "[FORMAT]");
    }

    private static String thumbnailUrl(String id) {
        return "https://cdnapisec.kaltura.com/p/2394162/thumbnail/entry_id/" + id + "/height/250/vid_slices/3/vid_slice/2";
    }
}
