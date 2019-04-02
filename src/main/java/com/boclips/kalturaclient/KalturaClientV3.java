package com.boclips.kalturaclient;

import com.boclips.kalturaclient.captionasset.*;
import com.boclips.kalturaclient.http.RequestFilters;
import com.boclips.kalturaclient.media.*;
import com.boclips.kalturaclient.session.SessionGenerator;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singleton;

public class KalturaClientV3 implements KalturaClient {
    private SessionGenerator sessionGenerator;
    private final MediaList mediaList;
    private final MediaDelete mediaDelete;
    private final MediaAdd mediaAdd;
    private final CaptionAssetList captionAssetList;
    private final CaptionAssetAdd captionAssetAdd;
    private final CaptionAssetSetContentClient captionAssetSetContent;
    private final CaptionAssetServeClient captionAssetServe;

    public KalturaClientV3(KalturaClientConfig config, SessionGenerator sessionGenerator) {
        this.sessionGenerator = sessionGenerator;
        this.mediaList = new MediaListClient(config);
        this.mediaDelete = new MediaDeleteClient(config);
        this.mediaAdd = new MediaAddClient(config);
        this.captionAssetList = new CaptionAssetListClient(config);
        this.captionAssetAdd = new CaptionAssetAddClient(config);
        this.captionAssetSetContent = new CaptionAssetSetContentClient(config);
        this.captionAssetServe = new CaptionAssetServeClient(config);
    }

    @Override
    public Map<String, List<MediaEntry>> getMediaEntriesByReferenceIds(Collection<String> referenceIds) {
        if(referenceIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<MediaEntry> mediaEntries = mediaList.get(this.sessionGenerator.get().getToken(), referenceIdIn(referenceIds));
        return mediaEntries.stream().collect(Collectors.groupingBy(MediaEntry::getReferenceId, Collectors.toList()));
    }

    @Override
    public void deleteMediaEntriesByReferenceId(String referenceId) {
        final List<MediaEntry> mediaEntryToBeDeleted = getMediaEntriesByReferenceId(referenceId);

        mediaEntryToBeDeleted.forEach(mediaEntry ->
                mediaDelete.deleteByEntityId(sessionGenerator.get().getToken(), mediaEntry.getId())
        );
    }

    @Override
    public void createMediaEntry(String referenceId) {
        mediaAdd.add(sessionGenerator.get().getToken(), referenceId);
    }

    @Override
    public void createCaptionsFile(String referenceId, CaptionAsset captionAsset, String content) {
        String entryId = entryIdFromReferenceId(referenceId);
        String token = sessionGenerator.get().getToken();
        CaptionAsset asset = captionAssetAdd.post(token, entryId, captionAsset);
        captionAssetSetContent.post(token, asset.getId(), content);
    }

    @Override
    public List<CaptionAsset> getCaptionFilesByReferenceId(String referenceId) {
        String entryId = entryIdFromReferenceId(referenceId);

        return captionAssetList.get(sessionGenerator.get().getToken(), entryIdEqual(entryId));
    }

    @Override
    public String getCaptionContentByAssetId(String assetId) {
        return captionAssetServe.get(sessionGenerator.get().getToken(), assetId);
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
