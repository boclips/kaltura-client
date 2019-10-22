package com.boclips.kalturaclient.media.list;

import com.boclips.kalturaclient.media.MediaEntry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
public class MediaEntryIterator implements Iterator {

    @NonNull
    private Iterator<List<MediaEntry>> pageIterator;

    private Iterator<MediaEntry> currentPage = null;

    @Override
    public boolean hasNext() {
        return (currentPage != null && currentPage.hasNext()) || pageIterator.hasNext();
    }

    @Override
    public MediaEntry next() {
        if (currentPage == null || (!currentPage.hasNext() && pageIterator.hasNext())) {
            currentPage = pageIterator.next().iterator();
        }

        return currentPage.next();
    }
}
