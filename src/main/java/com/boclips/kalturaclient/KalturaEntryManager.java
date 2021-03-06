package com.boclips.kalturaclient;

import com.boclips.kalturaclient.baseentry.BaseEntry;
import com.boclips.kalturaclient.media.MediaEntry;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface KalturaEntryManager {

    MediaEntry createEntry(String referenceId);
    Iterator<MediaEntry> getEntries();
    MediaEntry getEntry(String entryId);
    Map<String, MediaEntry> getEntries(Collection<String> entryIds);
    void deleteEntry(String entryId);
    BaseEntry getBaseEntry(String entryId);
    void tag(String entryId, List<String> tags);
    void setCategories(String entryId, List<String> categories);
    String addThumbnailFromImage(String entryId, InputStream fileStream, String filename);
    void setThumbnailAsDefault(String thumbAssetId);
}
