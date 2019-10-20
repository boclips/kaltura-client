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
            private Integer currentPage = 0;
            private Integer numberOfRequests = (int) Math.ceil(count.doubleValue() / pageSize.doubleValue());

            @Override
            public boolean hasNext() {
                return currentPage < numberOfRequests;
            }

            @Override
            public List<MediaEntry> next() {
                log.info("Fetching page {} with filters {}", currentPage, filters);
                List<MediaEntry> onePage = mediaList.get(merge(createPageFilters(currentPage), filters));
                currentPage++;
                log.info("Fetched {} of {} entries with filters {}", onePage.size(), count, filters);
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
