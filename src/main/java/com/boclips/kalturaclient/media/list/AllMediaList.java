package com.boclips.kalturaclient.media.list;

import com.boclips.kalturaclient.http.RequestFilters;
import com.boclips.kalturaclient.media.MediaEntry;
import com.boclips.kalturaclient.media.MediaList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.iterators.IteratorChain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.List;

import static com.boclips.kalturaclient.http.RequestFilters.merge;
import static java.util.Collections.emptyIterator;

/**
 * The Kaltura API has a hard limit of 10000 elements that can be returned per request.
 * <p>
 * Example: Requesting 2m videos will only work for page size and page index combinations which result in describing less than 10000 elements.
 * page size = 500, page index = 20 is valid
 * page size = 500, page index = 21 is invalid
 * page size = 100, page index = 100 is valid
 * page size = 100, page index = 101 is invalid
 * <p>
 * The invalid cases will yield the following error on Kaltura:
 * QUERY_EXCEEDED_MAX_MATCHES_ALLOWED: Unable to generate list. max matches value was reached
 * <p>
 * Problem: Kaltura can only return 10000 elements per request, this prevents clients to page through more than 10000 videos.
 * <p>
 * Solution: Page through catalogue, dicing it by time interval.
 * <p>
 * This class contains the logic to request an unbounded number of media entries.
 * <p>
 * Algorithm (essentially Binary Search):
 * 1) Define the time interval (beginning of time to now)
 * 2) Count the entries in interval
 * 3) If count is larger than 10000, go to 2) (recursive)
 * 4) If count is smaller than 10000, fetch all media entries (recursive base case)
 * 5) Return iterator of pages with equal or less than 10000 elements
 */
@Builder
@Slf4j
@AllArgsConstructor
public class AllMediaList {
    private MediaList mediaList;
    private Integer maxEntries;
    private Integer pageSize;

    public Iterator<List<MediaEntry>> get(RequestFilters searchFilters) {
        long beginningOfTime = LocalDateTime.of(2013, Month.JANUARY, 1, 0, 0, 0).toEpochSecond(ZoneOffset.UTC);
        long endOfTime = LocalDateTime.now().plusDays(1).toEpochSecond(ZoneOffset.UTC);

        log.info("Start fetching Media Entries from {} to {} with filters {}", beginningOfTime, endOfTime, searchFilters);
        return fetchOrDivide(searchFilters, beginningOfTime, endOfTime);
    }

    private Iterator<List<MediaEntry>> fetchOrDivide(RequestFilters filters, Long dateStart, Long dateEnd) {
        RequestFilters timeFilters = createTimeFilters(dateEnd, dateStart);
        Long numberOfEntriesForInterval = mediaList.count(merge(filters, timeFilters));

        log.info("Found {} entries to be fetched for current request ({} until {}})", numberOfEntriesForInterval, dateStart, dateEnd);

        if (numberOfEntriesForInterval == 0L) {
            log.info("Aborting execution as no videos in interval found");
            return emptyIterator();
        } else if (numberOfEntriesForInterval > maxEntries) {
            log.info("Splitting time interval in half as {} is greater than {}", numberOfEntriesForInterval, maxEntries);
            long mid = (dateEnd - dateStart) / 2;
            Iterator<List<MediaEntry>> leftInterval = fetchOrDivide(filters, dateStart, dateStart + mid);
            Iterator<List<MediaEntry>> rightInterval = fetchOrDivide(filters, dateStart + mid, dateEnd);
            return new IteratorChain<>(leftInterval, rightInterval);
        } else {
            log.info("Fetching {} entries for interval {} - {}",
                    numberOfEntriesForInterval,
                    Instant.ofEpochSecond(dateStart).atZone(ZoneOffset.UTC).toOffsetDateTime(),
                    Instant.ofEpochSecond(dateEnd).atZone(ZoneOffset.UTC).toOffsetDateTime());

            Iterator<List<MediaEntry>> result = new MediaEntryIterator(mediaList, pageSize, numberOfEntriesForInterval)
                    .getIterator(merge(filters, timeFilters));
            log.info("Results from time range ({} until {}) have the expected size {}", dateStart, dateEnd, numberOfEntriesForInterval);
            return result;
        }
    }

    private RequestFilters createTimeFilters(Long dateEnd, Long dateStart) {
        RequestFilters requestFilters = new RequestFilters();
        requestFilters.add(MediaFilterType.CREATED_AT_LESS_THAN_OR_EQUAL.getValue(), dateEnd.toString());
        requestFilters.add(MediaFilterType.CREATED_AT_GREATER_THAN_OR_EQUAL.getValue(), dateStart.toString());
        return requestFilters;
    }
}
