package com.boclips.kalturaclient;

import com.boclips.kalturaclient.baseentry.BaseEntry;
import com.boclips.kalturaclient.captionasset.CaptionAsset;
import com.boclips.kalturaclient.flavorAsset.Asset;
import com.boclips.kalturaclient.flavorParams.FlavorParams;
import com.boclips.kalturaclient.media.MediaEntry;
import com.boclips.kalturaclient.media.links.LinkBuilder;
import com.boclips.kalturaclient.session.RestSessionGenerator;
import com.boclips.kalturaclient.session.SessionRetriever;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface KalturaClient {
    static KalturaClient create(KalturaClientConfig config) {
        return KalturaClientV3.create(config, new RestSessionGenerator(new SessionRetriever(config), config.getSessionTtl()));
    }

    Iterator<MediaEntry> getEntries();

    List<Asset> getAssetsByEntryId(String entryId);

    Map<String, MediaEntry> getEntriesByIds(Collection<String> entryIds);

    Map<String, List<MediaEntry>> getEntriesByReferenceIds(Collection<String> referenceIds);

    MediaEntry getEntryById(String entryId);

    List<Asset> getAssetsByEntryIds(Collection<String> entryIds);

    List<MediaEntry> getEntriesByReferenceId(String referenceId);

    void deleteEntryById(String entryId);

    void deleteEntriesByReferenceId(String referenceId);

    void deleteAssetById(String assetId);

    void createEntry(String referenceId);

    CaptionAsset createCaptionsFileWithEntryId(String entryId, CaptionAsset captionAsset, String content);

    CaptionAsset createCaptionsFile(String referenceId, CaptionAsset captionAsset, String content);

    List<CaptionAsset> getCaptionFilesByEntryId(String entryId);

    List<CaptionAsset> getCaptionFilesByReferenceId(String referenceId);

    String getCaptionContentByAssetId(String assetId);

    void tag(String entryId, List<String> tags);

    BaseEntry getBaseEntry(String entryId);

    void deleteCaptionContentByAssetId(String assetId);

    List<FlavorParams> getFlavorParams();

    KalturaClientConfig getConfig();

    LinkBuilder getLinkBuilder();
}
