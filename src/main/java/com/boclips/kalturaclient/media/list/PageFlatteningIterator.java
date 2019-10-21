package com.boclips.kalturaclient.media.list;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
public class PageFlatteningIterator<T> implements Iterator<T> {

    @NonNull
    private Iterator<List<T>> pageIterator;

    private Iterator<T> currentPage = null;

    @Override
    public boolean hasNext() {
        return (currentPage != null && currentPage.hasNext()) || pageIterator.hasNext();
    }

    @Override
    public T next() {
        if (currentPage == null || (!currentPage.hasNext() && pageIterator.hasNext())) {
            currentPage = pageIterator.next().iterator();
        }

        return currentPage.next();
    }

}
