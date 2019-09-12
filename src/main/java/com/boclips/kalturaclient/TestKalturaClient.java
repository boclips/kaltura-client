package com.boclips.kalturaclient;

import com.boclips.kalturaclient.baseentry.BaseEntry;
import com.boclips.kalturaclient.captionasset.CaptionAsset;
import com.boclips.kalturaclient.media.MediaEntry;
import com.boclips.kalturaclient.media.MediaEntryStatus;
import com.boclips.kalturaclient.media.streams.StreamUrls;

import java.time.Duration;
import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 *
 */
public class TestKalturaClient implements KalturaClient {
    private final Map<String, List<MediaEntry>> mediaEntryListsByReferenceId = new HashMap<>();
    private final Map<String, List<CaptionAsset>> captionAssetsByReferenceId = new HashMap<>();
    private final Map<String, String> captionContentsByAssetId = new HashMap<>();
    private final Map<String, BaseEntry> baseEntriesByEntryId = new HashMap<>();

    @Override
    public Map<String, List<MediaEntry>> getMediaEntriesByReferenceIds(Collection<String> referenceIds) {
        return referenceIds.stream()
                .filter(mediaEntryListsByReferenceId::containsKey)
                .collect(toMap(referenceId -> referenceId, mediaEntryListsByReferenceId::get));
    }

    @Override
    public List<MediaEntry> getMediaEntriesByReferenceId(String referenceId) {
        return Optional.ofNullable(mediaEntryListsByReferenceId.get(referenceId)).orElse(Collections.emptyList());
    }

    @Override
    public void deleteMediaEntriesByReferenceId(String referenceId) {
        mediaEntryListsByReferenceId.remove(referenceId);
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
                .videoPreviewUrl(videoPreviewUrl(id))
                .status(MediaEntryStatus.NOT_READY)
                .build()
        );
    }

    @Override
    public CaptionAsset createCaptionsFile(String referenceId, CaptionAsset captionAsset, String content) {
        String assetId = UUID.randomUUID().toString();
        CaptionAsset copyWithId = captionAsset
                .toBuilder()
                .id(assetId)
                .build();
        captionAssetsByReferenceId.computeIfAbsent(referenceId, (refId) -> new ArrayList<>())
                .add(copyWithId);
        captionContentsByAssetId.put(assetId, content);
        return copyWithId;
    }

    @Override
    public List<CaptionAsset> getCaptionFilesByReferenceId(String referenceId) {
        List<CaptionAsset> captionFiles = captionAssetsByReferenceId.get(referenceId);
        if(captionFiles == null) {
            return Collections.emptyList();
        }
        return captionFiles;
    }

    @Override
    public String getCaptionContentByAssetId(String assetId) {
        return captionContentsByAssetId.get(assetId);
    }

    @Override
    public void deleteCaptionContentByAssetId(String assetId) {
        captionContentsByAssetId.remove(assetId);
        captionAssetsByReferenceId.values().forEach(assets -> {
            assets.stream()
                    .filter(asset -> asset.getId().equals(assetId))
                    .findAny()
                    .ifPresent(assets::remove);
        });
    }

    @Override
    public void tag(String entryId, List<String> tags) {
        baseEntriesByEntryId.put(
                entryId,
                BaseEntry.builder()
                        .id(entryId)
                        .tags(tags)
                        .build()
        );
    }

    @Override
    public BaseEntry getBaseEntry(String entryId) {
        return baseEntriesByEntryId.get(entryId);
    }

    public void addMediaEntry(MediaEntry mediaEntry) {
        mediaEntryListsByReferenceId
                .computeIfAbsent(mediaEntry.getReferenceId(), (String key) -> new ArrayList<>())
                .add(mediaEntry);
    }

    public void clear() {
        mediaEntryListsByReferenceId.clear();
        captionAssetsByReferenceId.clear();
        captionContentsByAssetId.clear();
        baseEntriesByEntryId.clear();
    }

    private static String downloadUrl(String id) {
        return "https://cdnapisec.kaltura.com/p/2394162/sp/239416200/playManifest/entryId/" + id + "/format/download/protocol/https/flavorParamIds/0";
    }

    private static StreamUrls streamUrl(String id) {
        return new StreamUrls("https://stream.com/s/" + id + "[FORMAT]");
    }

    private static String thumbnailUrl(String id) {
        return "https://cdnapisec.kaltura.com/p/2394162/thumbnail/entry_id/" + id + "/width/{thumbnailWidth}/vid_slices/3/vid_slice/1";
    }

    private static String videoPreviewUrl(String id) {
        return "https://cdnapisec.kaltura.com/p/2394162/thumbnail/entry_id/" + id + "/width/{thumbnailWidth}/vid_slices/{thumbnailCount}";
    }

}
