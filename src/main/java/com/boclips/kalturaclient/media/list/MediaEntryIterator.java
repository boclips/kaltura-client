package com.boclips.kalturaclient.media.list;

import com.boclips.kalturaclient.http.RequestFilters;
import com.boclips.kalturaclient.media.MediaEntry;
import com.boclips.kalturaclient.media.MediaList;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;

import static com.boclips.kalturaclient.http.RequestFilters.merge;

@Slf4j
@AllArgsConstructor
public class MediaEntryIterator {
    private MediaList mediaList;
    private Integer pageSize;
    private Long count;

    public Iterator<List<MediaEntry>> getIterator(RequestFilters filters) {
        return new Iterator<List<MediaEntry>>() {
            private Integer currentPage = 1;
            private Integer numberOfPages = (int) Math.ceil(count.doubleValue() / pageSize.doubleValue()) + 1;

            @Override
            public boolean hasNext() {
                return currentPage < numberOfPages;
            }

            @Override
            public List<MediaEntry> next() {
                log.info("Fetching page {} of {} with filters {}", currentPage, numberOfPages, filters);
                List<MediaEntry> onePage = mediaList.get(merge(createPageFilters(currentPage), filters));
                currentPage++;
                log.info("Fetched page {} of {} with {} out of {} entries with filters {}", currentPage, numberOfPages,onePage.size(), count, filters);
                return onePage;
            }
        };
    }

    private RequestFilters createPageFilters(Integer i) {
        RequestFilters pageFilters = new RequestFilters();
        pageFilters.add(MediaFilterType.PAGE_SIZE.getValue(), pageSize.toString());
        pageFilters.add(MediaFilterType.PAGE_INDEX.getValue(), i.toString());
        return pageFilters;
    }
}
