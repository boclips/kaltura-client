package com.boclips.kalturaclient.media.list

import com.boclips.kalturaclient.IteratorHelper
import com.boclips.kalturaclient.http.RequestFilters
import com.boclips.kalturaclient.media.MediaEntry
import com.boclips.kalturaclient.media.MediaList
import spock.lang.Specification

class MediaEntryIteratorTest extends Specification {
    def "fetches one page worth of Media Entries"() {
        given:
        def mediaEntries = Arrays.asList(
                MediaEntry.builder().id("1").build(),
                MediaEntry.builder().id("2").build()
        )
        def mediaListMock = Mock(MediaList) {
            1 * get(_) >> mediaEntries
        }

        when:
        def filters = new RequestFilters()
        def iterator = new MediaEntryIterator(mediaListMock, 100, 2).getIterator(filters)

        then:
        IteratorHelper.toList(iterator).size() == 2
    }

    def "fetches multiple pages worth of Media Entries"() {
        given:
        def mediaListMock = Mock(MediaList) {
            2 * get(_) >> Arrays.asList(MediaEntry.builder().id("1").build()) >> Arrays.asList(MediaEntry.builder().id("2").build())
        }

        when:
        def filters = new RequestFilters()
        def iterator = new MediaEntryIterator(mediaListMock, 1, 2).getIterator(filters)

        then:
        IteratorHelper.toList(iterator).size() == 2
    }

    def "zero count results in empty results"() {
        given:
        def mediaListMock = Mock(MediaList) {
            0 * get(_) >> _
        }

        when:
        def filters = new RequestFilters()
        def iterator = new MediaEntryIterator(mediaListMock, 1, 0).getIterator(filters)

        then:
        IteratorHelper.toList(iterator).size() == 0
    }
}
