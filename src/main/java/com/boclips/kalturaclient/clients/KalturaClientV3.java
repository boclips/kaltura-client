package com.boclips.kalturaclient.clients;

import com.boclips.kalturaclient.KalturaClient;
import com.boclips.kalturaclient.baseentry.*;
import com.boclips.kalturaclient.captionasset.*;
import com.boclips.kalturaclient.config.KalturaClientConfig;
import com.boclips.kalturaclient.flavorAsset.*;
import com.boclips.kalturaclient.flavorParams.FlavorParams;
import com.boclips.kalturaclient.flavorParams.FlavorParamsListClient;
import com.boclips.kalturaclient.http.KalturaRestClient;
import com.boclips.kalturaclient.http.RequestFilters;
import com.boclips.kalturaclient.media.*;
import com.boclips.kalturaclient.media.links.LinkBuilder;
import com.boclips.kalturaclient.media.list.AllMediaList;
import com.boclips.kalturaclient.session.SessionGenerator;
import org.apache.http.annotation.Experimental;

import java.util.*;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

public class KalturaClientV3 implements KalturaClient {
    private final MediaList mediaList;
    private final AllMediaList allMediaList;
    private final MediaDelete mediaDelete;
    private final FlavorAssetDelete flavorAssetDelete;
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
    private final FlavorAssetList flavorAssetList;

    public static KalturaClientV3 create(KalturaClientConfig config, SessionGenerator sessionGenerator) {
        KalturaRestClient client = KalturaRestClient.create(config.getBaseUrl(), sessionGenerator);

        return new KalturaClientV3(client, config);
    }

    KalturaClientV3(KalturaRestClient restClient, KalturaClientConfig config) {
        this.config = config;

        this.baseEntryGet = new BaseEntryGetClient(restClient);
        this.baseEntryUpdate = new BaseEntryUpdateClient(restClient);

        this.mediaList = new MediaListClient(restClient);
        this.allMediaList = new AllMediaList(this.mediaList, 9500, 500);

        this.mediaDelete = new MediaDeleteClient(restClient);
        this.mediaAdd = new MediaAddClient(restClient);

        this.flavorParamsList = new FlavorParamsListClient(restClient);
        this.flavorParams = flavorParamsList.get();

        this.flavorAssetDelete = new FlavorAssetDeleteClient(restClient);

        this.captionAssetList = new CaptionAssetListClient(restClient);
        this.captionAssetAdd = new CaptionAssetAddClient(restClient);
        this.captionAssetDelete = new CaptionAssetDelete(restClient);
        this.captionAssetSetContent = new CaptionAssetSetContentClient(restClient);
        this.captionAssetServe = new CaptionAssetServeClient(restClient);

        this.linkBuilder = new LinkBuilder(this);

        this.flavorAssetList = new FlavorAssetListClient(restClient);
    }

    @Experimental
    @Override
    public Iterator<MediaEntry> getEntries() {
        return allMediaList.get(new RequestFilters());
    }


    @Override
    public List<Asset> getVideoAssets(String entryId) {
        return flavorAssetList.list(entryIdEqual(entryId));
    }

    @Override
    public MediaEntry getEntry(String entryId) {
        List<MediaEntry> mediaEntries = mediaList.get(idEqual(entryId));
        if (mediaEntries.isEmpty()) {
            return null;
        }

        return mediaEntries.get(0);
    }

    @Override
    public Map<String, List<Asset>> getVideoAssets(Collection<String> entryIds) {
        if (entryIds.isEmpty()) {
            return emptyMap();
        }
        int pageSize = 500;
        int pageIndex = 1;
        int maxEntries = pageSize / 10;
        if (entryIds.size() > maxEntries) {
            throw new IllegalArgumentException("Too many entry ids. Max " + maxEntries + ", got " + entryIds.size());
        }
        List<Asset> assets = flavorAssetList.list(page(pageSize, pageIndex, entryIdIn(entryIds)));

        Map<String, List<Asset>> assetsByEntryId = new HashMap<>();

        assets.forEach(asset -> {
            if (!assetsByEntryId.containsKey(asset.getEntryId())) {
                assetsByEntryId.put(asset.getEntryId(), new ArrayList<>());
            }
            assetsByEntryId.get(asset.getEntryId()).add(asset);
        });

        return assetsByEntryId;
    }

    @Override
    public Map<String, MediaEntry> getEntries(Collection<String> entryIds) {
        if (entryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<MediaEntry> mediaEntries = mediaList.get(idIn(entryIds));
        return mediaEntries.stream().collect(toMap(MediaEntry::getId, mediaEntry -> mediaEntry));
    }

    @Override
    public void deleteVideoAsset(String assetId) {
        flavorAssetDelete.deleteByAssetId(assetId);
    }

    @Override
    public void deleteEntry(String entryId) {
        mediaDelete.deleteByEntryId(entryId);
    }

    @Override
    public MediaEntry createEntry(String referenceId) {
        return mediaAdd.add(referenceId);
    }

    @Override
    public CaptionAsset createCaptionForVideo(String entryId, CaptionAsset captionAsset, String content) {
        CaptionAsset asset = captionAssetAdd.post(entryId, captionAsset);
        return captionAssetSetContent.post(asset.getId(), content);
    }


    @Override
    public List<CaptionAsset> getCaptionsForVideo(String entryId) {
        return captionAssetList.get(entryIdEqual(entryId));
    }

    @Override
    public String getCaptionContent(String captionAssetId) {
        return captionAssetServe.get(captionAssetId);
    }

    @Override
    public void deleteCaption(String captionAssetId) {
        captionAssetDelete.post(captionAssetId);
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

    private RequestFilters entryIdEqual(String entryId) {
        return new RequestFilters()
                .add("filter[entryIdEqual]", entryId);
    }

    private RequestFilters entryIdIn(Collection<String> entryIds) {
        return new RequestFilters()
                .add("filter[entryIdIn]", String.join(",", entryIds));
    }

    private RequestFilters idIn(Collection<String> entryIds) {
        return new RequestFilters()
                .add("filter[idIn]", String.join(",", entryIds));
    }

    private RequestFilters idEqual(String entryId) {
        return new RequestFilters()
                .add("filter[idEqual]", entryId);
    }

    private RequestFilters page(Integer pageSize, Integer pageIndex, RequestFilters filters) {
        return filters
                .add("pager[pageSize]", pageSize.toString())
                .add("pager[pageIndex]", pageIndex.toString());
    }

    public List<FlavorParams> getFlavorParams() {
        return flavorParams;
    }

    @Override
    public KalturaClientConfig getConfig() {
        return this.config;
    }
}
