package com.boclips.kalturaclient;

import com.boclips.kalturaclient.http.RequestFilters;
import com.boclips.kalturaclient.media.*;
import com.boclips.kalturaclient.session.SessionGenerator;

import java.util.*;
import java.util.stream.Collectors;

public class KalturaClientV3 implements KalturaClient {
    private SessionGenerator sessionGenerator;
    private final MediaList mediaList;
    private final MediaDelete mediaDelete;
    private final MediaAdd mediaAdd;

    public KalturaClientV3(KalturaClientConfig config, SessionGenerator sessionGenerator) {
        this.sessionGenerator = sessionGenerator;
        this.mediaList = new MediaListClient(config);
        this.mediaDelete = new MediaDeleteClient(config);
        this.mediaAdd = new MediaAddClient(config);
    }

    @Override
    public Map<String, List<MediaEntry>> getMediaEntriesByReferenceIds(Collection<String> referenceIds) {
        List<MediaEntry> mediaEntries = mediaList.get(this.sessionGenerator.get().getToken(), createFilters(referenceIds));
        return mediaEntries.stream().collect(Collectors.groupingBy(MediaEntry::getReferenceId, Collectors.toList()));
    }

    @Override
    public void deleteMediaEntriesByReferenceId(String referenceId) {
        final List<MediaEntry> mediaEntryToBeDeleted = getMediaEntriesByReferenceId(referenceId);

        mediaEntryToBeDeleted.forEach(mediaEntry -> {
            mediaDelete.deleteByReferenceId(sessionGenerator.get().getToken(), mediaEntry.getId());
        });
    }

    @Override
    public void createMediaEntry(String referenceId) {
        mediaAdd.add(sessionGenerator.get().getToken(), referenceId);
    }

    @Override
    public List<MediaEntry> getMediaEntriesByReferenceId(String referenceId) {
        return Optional.ofNullable(getMediaEntriesByReferenceIds(Collections.singleton(referenceId)).get(referenceId))
                .orElse(Collections.emptyList());
    }

    private RequestFilters createFilters(Collection<String> referenceIds) {
        return new RequestFilters()
                .add("filter[referenceIdIn]", String.join(",", referenceIds));
    }
}
