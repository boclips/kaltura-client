package com.boclips.kalturaclient;

import com.boclips.kalturaclient.baseentry.BaseEntry;
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

public interface KalturaEntryManager {

    Iterator<MediaEntry> getEntries();

    List<Asset> getAssetsByEntryId(String entryId);

    Map<String, MediaEntry> getEntriesByIds(Collection<String> entryIds);

    Map<String, List<MediaEntry>> getEntriesByReferenceIds(Collection<String> referenceIds);

    MediaEntry getEntryById(String entryId);

    Map<String, List<Asset>> getAssetsByEntryIds(Collection<String> entryIds);

    List<MediaEntry> getEntriesByReferenceId(String referenceId);

    void deleteEntryById(String entryId);

    void deleteEntriesByReferenceId(String referenceId);

    void deleteAssetById(String assetId);

    void createEntry(String referenceId);

    BaseEntry getBaseEntry(String entryId);
}
