package com.boclips.kalturaclient.media.list


import com.boclips.kalturaclient.http.RequestFilters
import com.boclips.kalturaclient.media.MediaEntry
import com.boclips.kalturaclient.media.MediaList
import spock.lang.Specification

class AllMediaListTest extends Specification {
    def "fetch nothing"() {
        given:
        def mediaEntryMock = Mock(MediaList) {
            count(_) >> 0
        }
        def allMediaList = new AllMediaList(mediaEntryMock, 5, 10)

        when:
        def iterator = allMediaList.get(new RequestFilters())

        then:
        iterator.toList().size() == 0
    }

    def "fetch intervals"() {
        given:
        def mediaEntryMock = Mock(MediaList) {
            3 * count(_) >> 6 >> 3 >> 3
            2 * get(_) >> Arrays.asList(
                    MediaEntry.builder().id("1").build(),
                    MediaEntry.builder().id("2").build(),
                    MediaEntry.builder().id("2").build()
            )
        }
        def allMediaList = new AllMediaList(mediaEntryMock, 5, 3)

        when:
        Iterator<MediaEntry> iterator = allMediaList.get(new RequestFilters())

        then:
        def mediaEntries = iterator.toList()
        mediaEntries.size() == 6
    }

    def "fetch intervals with paging"() {
        given:
        def mediaEntryMock = Mock(MediaList) {
            3 * count(_) >> 6 >> 3 >> 3
            6 * get(_) >> Arrays.asList(MediaEntry.builder().id("1").build())
        }
        def allMediaList = new AllMediaList(mediaEntryMock, 5, 1)

        when:
        Iterator<MediaEntry> iterator = allMediaList.get(new RequestFilters())

        then:
        def mediaEntries = iterator.toList()
        mediaEntries.size() == 6
    }
}
