package com.boclips.kalturaclient.media.list

import com.boclips.kalturaclient.media.MediaEntry
import spock.lang.Specification

class MediaEntryIteratorTest extends Specification {

    def "it returns elements from a single page"() {
        given:
        def testIterator = generatePageIterator(
                Arrays.asList(
                        Arrays.asList(
                                MediaEntry.builder().id("1").build(),
                                MediaEntry.builder().id("2").build())
                                as List<MediaEntry>
                )
        )

        when:
        MediaEntryIterator flatIterator = new MediaEntryIterator(testIterator)

        then:
        flatIterator.hasNext()
        flatIterator.next().id == "1"
        flatIterator.hasNext()
        flatIterator.next().id == "2"
        !flatIterator.hasNext()
    }

    def "it returns elements from two pages"() {
        given:
        def testIterator = generatePageIterator(
                Arrays.<List<MediaEntry>> asList(
                        Arrays.asList(
                                MediaEntry.builder().id("1").build(),
                                MediaEntry.builder().id("2").build())
                                as List<MediaEntry>,
                        Arrays.asList(
                                MediaEntry.builder().id("A").build(),
                                MediaEntry.builder().id("B").build())
                                as List<MediaEntry>
                )
        )

        when:
        MediaEntryIterator flatIterator = new MediaEntryIterator(testIterator)

        then:
        flatIterator.hasNext()
        flatIterator.next().id == "1"
        flatIterator.hasNext()
        flatIterator.next().id == "2"
        flatIterator.hasNext()
        flatIterator.next().id == "A"
        flatIterator.hasNext()
        flatIterator.next().id == "B"
        !flatIterator.hasNext()
    }

    private Iterator generatePageIterator(List<List<MediaEntry>> pages) {
        new Iterator() {
            private currentIndex = 0;

            @Override
            boolean hasNext() {
                return currentIndex < pages.size();
            }

            @Override
            List<MediaEntry> next() {
                return pages.get(currentIndex++);
            }
        }
    }
}
