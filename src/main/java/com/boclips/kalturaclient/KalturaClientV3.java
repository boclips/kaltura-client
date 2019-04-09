package com.boclips.kalturaclient;

import com.boclips.kalturaclient.baseentry.*;
import com.boclips.kalturaclient.captionasset.*;
import com.boclips.kalturaclient.http.HttpClient;
import com.boclips.kalturaclient.http.RequestFilters;
import com.boclips.kalturaclient.media.*;
import com.boclips.kalturaclient.session.SessionGenerator;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singleton;

public class KalturaClientV3 implements KalturaClient {
    private final MediaList mediaList;
    private final MediaDelete mediaDelete;
    private final MediaAdd mediaAdd;
    private final CaptionAssetList captionAssetList;
    private final CaptionAssetAdd captionAssetAdd;
    private final CaptionAssetDelete captionAssetDelete;
    private final CaptionAssetSetContentClient captionAssetSetContent;
    private final CaptionAssetServeClient captionAssetServe;
    private final BaseEntryGet baseEntryGet;
    private final BaseEntryUpdate baseEntryUpdate;

    public KalturaClientV3(KalturaClientConfig config, SessionGenerator sessionGenerator) {
        HttpClient client = new HttpClient(config.getBaseUrl(), sessionGenerator);
        this.mediaList = new MediaListClient(client, config);
        this.mediaDelete = new MediaDeleteClient(client);
        this.mediaAdd = new MediaAddClient(client);
        this.captionAssetList = new CaptionAssetListClient(client);
        this.captionAssetAdd = new CaptionAssetAddClient(client);
        this.captionAssetDelete = new CaptionAssetDelete(client);
        this.captionAssetSetContent = new CaptionAssetSetContentClient(client);
        this.captionAssetServe = new CaptionAssetServeClient(client);
        this.baseEntryGet = new BaseEntryGetClient(client);
        this.baseEntryUpdate = new BaseEntryUpdateClient(client);
    }

    @Override
    public Map<String, List<MediaEntry>> getMediaEntriesByReferenceIds(Collection<String> referenceIds) {
        if(referenceIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<MediaEntry> mediaEntries = mediaList.get(referenceIdIn(referenceIds));
        return mediaEntries.stream().collect(Collectors.groupingBy(MediaEntry::getReferenceId, Collectors.toList()));
    }

    @Override
    public void deleteMediaEntriesByReferenceId(String referenceId) {
        final List<MediaEntry> mediaEntryToBeDeleted = getMediaEntriesByReferenceId(referenceId);

        mediaEntryToBeDeleted.forEach(mediaEntry ->
                mediaDelete.deleteByEntityId(mediaEntry.getId())
        );
    }

    @Override
    public void createMediaEntry(String referenceId) {
        mediaAdd.add(referenceId);
    }

    @Override
    public CaptionAsset createCaptionsFile(String referenceId, CaptionAsset captionAsset, String content) {
        String entryId = entryIdFromReferenceId(referenceId);
        CaptionAsset asset = captionAssetAdd.post(entryId, captionAsset);
        return captionAssetSetContent.post(asset.getId(), content);
    }

    @Override
    public List<CaptionAsset> getCaptionFilesByReferenceId(String referenceId) {
        String entryId = entryIdFromReferenceId(referenceId);

        return captionAssetList.get(entryIdEqual(entryId));
    }

    @Override
    public String getCaptionContentByAssetId(String assetId) {
        return captionAssetServe.get(assetId);
    }

    @Override
    public void deleteCaptionContentByAssetId(String assetId) {
        captionAssetDelete.post(assetId);
    }

    @Override
    public void tag(String entryId, List<String> tags) {
        baseEntryUpdate.post(entryId, BaseEntry.builder().id(entryId).tags(tags).build());
    }

    @Override
    public BaseEntry getBaseEntry(String entryId) {
        return baseEntryGet.get(entryId);
    }

    private String entryIdFromReferenceId(String referenceId) {
        List<MediaEntry> mediaEntries = getMediaEntriesByReferenceId(referenceId);

        if(mediaEntries.size() != 1) {
            throw new RuntimeException(mediaEntries.size() + " media entries for reference id " + referenceId);
        }

        MediaEntry mediaEntry = mediaEntries.get(0);

        return mediaEntry.getId();
    }

    @Override
    public List<MediaEntry> getMediaEntriesByReferenceId(String referenceId) {
        return Optional.ofNullable(getMediaEntriesByReferenceIds(singleton(referenceId)).get(referenceId))
                .orElse(Collections.emptyList());
    }

    private RequestFilters entryIdEqual(String entryId) {
        return new RequestFilters()
                .add("filter[entryIdEqual]", entryId);
    }

    private RequestFilters referenceIdIn(Collection<String> referenceIds) {
        return new RequestFilters()
                .add("filter[referenceIdIn]", String.join(",", referenceIds));
    }
}
