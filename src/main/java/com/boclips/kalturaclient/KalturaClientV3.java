package com.boclips.kalturaclient;

import com.boclips.kalturaclient.baseentry.*;
import com.boclips.kalturaclient.captionasset.*;
import com.boclips.kalturaclient.flavorParams.FlavorParams;
import com.boclips.kalturaclient.flavorParams.FlavorParamsListClient;
import com.boclips.kalturaclient.http.KalturaClientApiException;
import com.boclips.kalturaclient.http.KalturaRestClient;
import com.boclips.kalturaclient.http.RequestFilters;
import com.boclips.kalturaclient.media.*;
import com.boclips.kalturaclient.media.links.LinkBuilder;
import com.boclips.kalturaclient.media.list.AllMediaList;
import com.boclips.kalturaclient.session.SessionGenerator;
import org.apache.http.annotation.Experimental;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toMap;

public class KalturaClientV3 implements KalturaClient {
    private final MediaList mediaList;
    private final AllMediaList allMediaList;
    private final MediaDelete mediaDelete;
    private final MediaAdd mediaAdd;
    private final CaptionAssetList captionAssetList;
    private final CaptionAssetAdd captionAssetAdd;
    private final CaptionAssetDelete captionAssetDelete;
    private final CaptionAssetSetContentClient captionAssetSetContent;
    private final CaptionAssetServeClient captionAssetServe;
    private final BaseEntryGet baseEntryGet;
    private final BaseEntryUpdate baseEntryUpdate;
    private final LinkBuilder linkBuilder;
    private final FlavorParamsListClient flavorParamsList;
    private final KalturaClientConfig config;
    private final List<FlavorParams> flavorParams;

    public static KalturaClientV3 create(KalturaClientConfig config, SessionGenerator sessionGenerator) {
        KalturaRestClient client = KalturaRestClient.create(config.getBaseUrl(), sessionGenerator);

        return new KalturaClientV3(client, config);
    }

    KalturaClientV3(KalturaRestClient restClient, KalturaClientConfig config) {
        this.config = config;

        this.baseEntryGet = new BaseEntryGetClient(restClient);
        this.baseEntryUpdate = new BaseEntryUpdateClient(restClient);

        this.mediaList = new MediaListClient(restClient);
        this.allMediaList = new AllMediaList(this.mediaList, 1000, 100);

        this.mediaDelete = new MediaDeleteClient(restClient);
        this.mediaAdd = new MediaAddClient(restClient);

        this.flavorParamsList = new FlavorParamsListClient(restClient);

        this.captionAssetList = new CaptionAssetListClient(restClient);
        this.captionAssetAdd = new CaptionAssetAddClient(restClient);
        this.captionAssetDelete = new CaptionAssetDelete(restClient);
        this.captionAssetSetContent = new CaptionAssetSetContentClient(restClient);
        this.captionAssetServe = new CaptionAssetServeClient(restClient);

        this.linkBuilder = new LinkBuilder(this);

        this.flavorParams = flavorParamsList.get();
    }

    @Override
    public Iterator<List<MediaEntry>> getMediaEntries() {
        return allMediaList.get(new RequestFilters());
    }

    @Override
    public MediaEntry getMediaEntryById(String entryId) {
        List<MediaEntry> mediaEntries = mediaList.get(idEqual(entryId));
        if (mediaEntries.isEmpty()) {
            return null;
        }

        return mediaEntries.get(0);
    }

    @Override
    public Map<String, MediaEntry> getMediaEntriesByIds(Collection<String> entryIds) {
        if (entryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<MediaEntry> mediaEntries = mediaList.get(idIn(entryIds));
        return mediaEntries.stream().collect(toMap(MediaEntry::getId, mediaEntry -> mediaEntry));
    }

    @Override
    public Map<String, List<MediaEntry>> getMediaEntriesByReferenceIds(Collection<String> referenceIds) {
        if (referenceIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<MediaEntry> mediaEntries = mediaList.get(referenceIdIn(referenceIds));
        return mediaEntries.stream().collect(Collectors.groupingBy(MediaEntry::getReferenceId, Collectors.toList()));
    }

    @Override
    public void deleteMediaEntryById(String entryId) {
        mediaDelete.deleteByEntryId(entryId);
    }

    @Override
    public void deleteMediaEntriesByReferenceId(String referenceId) {
        final List<MediaEntry> mediaEntriesToBeDeleted = getMediaEntriesByReferenceId(referenceId);

        List<KalturaClientApiException> errors = new ArrayList<>();

        mediaEntriesToBeDeleted.forEach(mediaEntry -> {
                    try {
                        deleteMediaEntryById(mediaEntry.getId());
                    } catch (KalturaClientApiException e) {
                        errors.add(e);
                    }
                }
        );

        if (errors.size() > 0) {
            throw errors.get(0);
        }
    }

    @Override
    public void createMediaEntry(String referenceId) {
        mediaAdd.add(referenceId);
    }

    @Override
    public CaptionAsset createCaptionsFileWithEntryId(String entryId, CaptionAsset captionAsset, String content) {
        CaptionAsset asset = captionAssetAdd.post(entryId, captionAsset);
        return captionAssetSetContent.post(asset.getId(), content);
    }

    @Override
    public CaptionAsset createCaptionsFile(String referenceId, CaptionAsset captionAsset, String content) {
        String entryId = entryIdFromReferenceId(referenceId);
        return createCaptionsFileWithEntryId(entryId, captionAsset, content);
    }

    @Override
    public List<CaptionAsset> getCaptionFilesByEntryId(String entryId) {
        return captionAssetList.get(entryIdEqual(entryId));
    }

    @Override
    public List<CaptionAsset> getCaptionFilesByReferenceId(String referenceId) {
        String entryId = entryIdFromReferenceId(referenceId);

        return getCaptionFilesByEntryId(entryId);
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
    public LinkBuilder getLinkBuilder() {
        return linkBuilder;
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

        if (mediaEntries.size() != 1) {
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

    private RequestFilters idIn(Collection<String> entryIds) {
        return new RequestFilters()
                .add("filter[idIn]", String.join(",", entryIds));
    }

    private RequestFilters idEqual(String entryId) {
        return new RequestFilters()
                .add("filter[idEqual]", entryId);
    }

    private RequestFilters referenceIdIn(Collection<String> referenceIds) {
        return new RequestFilters()
                .add("filter[referenceIdIn]", String.join(",", referenceIds));
    }

    public List<FlavorParams> getFlavorParams() {
        return flavorParams;
    }

    @Override
    public KalturaClientConfig getConfig() {
        return this.config;
    }
}
