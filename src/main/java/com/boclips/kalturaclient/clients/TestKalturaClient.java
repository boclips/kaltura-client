package com.boclips.kalturaclient.clients;

import com.boclips.kalturaclient.KalturaClient;
import com.boclips.kalturaclient.baseentry.BaseEntry;
import com.boclips.kalturaclient.captionasset.CaptionAsset;
import com.boclips.kalturaclient.config.KalturaClientConfig;
import com.boclips.kalturaclient.flavorAsset.Asset;
import com.boclips.kalturaclient.flavorParams.FlavorParams;
import com.boclips.kalturaclient.flavorParams.Quality;
import com.boclips.kalturaclient.media.MediaEntry;
import com.boclips.kalturaclient.media.MediaEntryStatus;
import com.boclips.kalturaclient.media.links.LinkBuilder;
import lombok.SneakyThrows;
import org.apache.http.annotation.Experimental;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class TestKalturaClient implements KalturaClient {
    private final Map<String, MediaEntry> mediaEntriesById = new HashMap<>();
    private final Map<String, List<CaptionAsset>> captionAssetsByReferenceId = new HashMap<>();
    private final Map<String, List<CaptionAsset>> captionAssetsByEntryId = new HashMap<>();
    private final Map<String, String> captionContentsByAssetId = new HashMap<>();
    private final Map<String, BaseEntry> baseEntriesByEntryId = new HashMap<>();
    private final Map<String, List<Asset>> assetsByEntryId = new HashMap<>();
    private final Map<String, String> entryIdsByThumbAssetId = new HashMap<>();
    private final LinkBuilder linkBuilder;
    private KalturaClientConfig config;

    public TestKalturaClient() {
        config = KalturaClientConfig.builder()
                .partnerId("partner-id")
                .userId("user-id")
                .secret("ssh-it-is-a-secret")
                .build();
        linkBuilder = new LinkBuilder(this);
    }

    @Experimental
    @Override
    public Iterator<MediaEntry> getEntries() {
        return new ArrayList<>(mediaEntriesById.values()).iterator();
    }

    @Override
    public List<Asset> getVideoAssets(String entryId) {
        return assetsByEntryId.get(entryId);
    }

    @Override
    public Map<String, MediaEntry> getEntries(Collection<String> entryIds) {
        return entryIds.stream()
                .filter(mediaEntriesById::containsKey)
                .collect(toMap(entryId -> entryId, mediaEntriesById::get));
    }

    @Override
    public MediaEntry getEntry(String entryId) {
        return mediaEntriesById.get(entryId);
    }

    @Override
    public Map<String, List<Asset>> getVideoAssets(Collection<String> entryIds) {
        return assetsByEntryId.entrySet().stream()
                .filter(entry -> entryIds.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteEntry(String entryId) {
        MediaEntry mediaEntry = mediaEntriesById.get(entryId);
        mediaEntriesById.remove(entryId);
    }

    @Override
    public void deleteVideoAsset(String assetId) {
    }

    @Override
    public MediaEntry createEntry(String referenceId) {
        String id = UUID.randomUUID().toString();

        createMediaEntry(id, referenceId, Duration.ofSeconds(92), MediaEntryStatus.NOT_READY);
        return getEntry(id);
    }

    @SuppressWarnings("WeakerAccess")
    public void createMediaEntry(String id, String referenceId, Duration duration, MediaEntryStatus status) {
        createMediaEntry(MediaEntry.builder()
                .referenceId(referenceId)
                .id(id)
                .downloadUrl(downloadUrl(id))
                .duration(duration)
                .status(status)
                .playCount(0)
                .tags(Collections.emptyList())
                .flavorParamsIds(Arrays.asList("1", "2", "3", "4"))
                .createdAt(ZonedDateTime.now())
                .conversionProfileId(1234560)
                .build()
        );
    }

    @SuppressWarnings("WeakerAccess")
    public void createMediaEntry(MediaEntry mediaEntry) {
        addMediaEntry(mediaEntry);
    }

    @Override
    public CaptionAsset createCaptionForVideo(String entryId, CaptionAsset captionAsset, String content) {
        MediaEntry mediaEntry = getEntry(entryId);

        String assetId = UUID.randomUUID().toString();
        CaptionAsset captionAssetWithId = captionAsset
                .toBuilder()
                .id(assetId)
                .build();

        captionAssetsByReferenceId.computeIfAbsent(mediaEntry.getReferenceId(), (refId) -> new ArrayList<>())
                .add(captionAssetWithId);

        captionAssetsByEntryId.computeIfAbsent(entryId, (id) -> new ArrayList<>())
                .add(captionAssetWithId);

        captionContentsByAssetId.put(assetId, content);
        return captionAssetWithId;
    }

    @Override
    public List<CaptionAsset> getCaptionsForVideo(String entryId) {
        List<CaptionAsset> captionFiles = captionAssetsByEntryId.get(entryId);
        if (captionFiles == null) {
            return Collections.emptyList();
        }
        return captionFiles;
    }

    @Override
    public String getCaptionContent(String captionAssetId) {
        return captionContentsByAssetId.get(captionAssetId);
    }

    @Override
    public URI getCaptionAssetUrl(String captionAssetId) {
        try {
            return new URI("https://caption-download-link/" + captionAssetId);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @Override
    public void deleteCaption(String captionAssetId) {
        captionContentsByAssetId.remove(captionAssetId);

        captionAssetsByReferenceId.values().forEach(assets -> assets.stream()
                .filter(asset -> asset.getId().equals(captionAssetId))
                .findAny()
                .ifPresent(assets::remove));

        captionAssetsByEntryId.values().forEach(assets -> assets.stream()
                .filter(asset -> asset.getId().equals(captionAssetId))
                .findAny()
                .ifPresent(assets::remove));
    }

    @Override
    public LinkBuilder getLinkBuilder() {
        return linkBuilder;
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
    public String addThumbnailFromImage(String entryId, InputStream fileStream, String filename) {
        String thumbAssetId = "thumbAssetId_" + filename;
        entryIdsByThumbAssetId.put(thumbAssetId, entryId);
        return baseEntriesByEntryId.containsKey(entryId)
                ? thumbAssetId
                : null;
    }

    @Override
    public void setThumbnailAsDefault(String thumbAssetId) {
        String entryId = entryIdsByThumbAssetId.get(thumbAssetId);
        if (baseEntriesByEntryId.containsKey(entryId)) {
            BaseEntry baseEntry = baseEntriesByEntryId.get(entryId);
            BaseEntry updatedUrlBaseEntry = BaseEntry.builder()
                    .id(baseEntry.getId())
                    .tags(baseEntry.getTags())
                    .thumbnailUrl(thumbAssetId)
                    .build();
            baseEntriesByEntryId.put(entryId, updatedUrlBaseEntry);
        }
    }

    @Override
    public BaseEntry getBaseEntry(String entryId) {
        return baseEntriesByEntryId.get(entryId);
    }

    @Override
    public List<FlavorParams> getFlavorParams() {
        return Arrays.asList(
                FlavorParams.builder()
                        .id(487041)
                        .height(360)
                        .width(0)
                        .quality(Quality.LOW)
                        .build(),
                FlavorParams.builder()
                        .id(487071)
                        .height(720)
                        .width(0)
                        .quality(Quality.MEDIUM)
                        .build(),
                FlavorParams.builder()
                        .id(487081)
                        .height(720)
                        .width(0)
                        .quality(Quality.HIGH)
                        .build(),
                FlavorParams.builder()
                        .id(487091)
                        .height(1080)
                        .width(0)
                        .quality(Quality.HIGH)
                        .build()
        );
    }

    @Override
    public KalturaClientConfig getConfig() {
        return this.config;
    }

    public void addMediaEntry(MediaEntry mediaEntry) {
        mediaEntriesById.put(mediaEntry.getId(), mediaEntry);
        baseEntriesByEntryId.put(
                mediaEntry.getId(),
                BaseEntry.builder().id(mediaEntry.getId()).thumbnailUrl("defaultThumbnailUrl").build());
    }

    @SneakyThrows
    @Override
    public URI getDownloadAssetUrl(String assetId) {
        if (assetsByEntryId.values().stream()
                .flatMap(Collection::stream)
                .anyMatch(asset -> asset.getId().equals(assetId))) {
            return new URI("/asset-download/" + assetId + ".mp4");
        }
        return null;
    }

    public void setAssets(String entryId, List<Asset> assets) {
        assetsByEntryId.put(entryId, assets);
    }

    public void clear() {
        mediaEntriesById.clear();
        captionAssetsByReferenceId.clear();
        captionAssetsByEntryId.clear();
        captionContentsByAssetId.clear();
        baseEntriesByEntryId.clear();
        assetsByEntryId.clear();
    }

    private static String downloadUrl(String id) {
        return "https://download.com/entryId/" + id + "/format/download";
    }
}
