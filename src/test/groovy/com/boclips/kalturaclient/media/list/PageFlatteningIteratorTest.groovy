package com.boclips.kalturaclient.media.list

import spock.lang.Specification

class PageFlatteningIteratorTest extends Specification {

    def "it returns elements from a single page"() {
        given:
        def testIterator = generatePageIterator(
                Arrays.<List<String>> asList(
                        Arrays.asList("One", "Two", "Three", "Four", "Five")
                )
        )

        when:
        PageFlatteningIterator flatIterator = new PageFlatteningIterator(testIterator)

        then:
        flatIterator.hasNext()
        flatIterator.next() == "One"
        flatIterator.hasNext()
        flatIterator.next() == "Two"
        flatIterator.hasNext()
        flatIterator.next() == "Three"
        flatIterator.hasNext()
        flatIterator.next() == "Four"
        flatIterator.hasNext()
        flatIterator.next() == "Five"
        !flatIterator.hasNext()
    }

    def "it returns elements from two pages"() {
        given:
        def testIterator = generatePageIterator(
                Arrays.<List<String>> asList(
                        Arrays.asList("One", "Two", "Three", "Four", "Five"),
                        Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H")
                )
        )

        when:
        PageFlatteningIterator flatIterator = new PageFlatteningIterator(testIterator)

        then:
        flatIterator.hasNext()
        flatIterator.next() == "One"
        flatIterator.hasNext()
        flatIterator.next() == "Two"
        flatIterator.hasNext()
        flatIterator.next() == "Three"
        flatIterator.hasNext()
        flatIterator.next() == "Four"
        flatIterator.hasNext()
        flatIterator.next() == "Five"
        flatIterator.hasNext()
        flatIterator.next() == "A"
        flatIterator.hasNext()
        flatIterator.next() == "B"
        flatIterator.hasNext()
        flatIterator.next() == "C"
        flatIterator.hasNext()
        flatIterator.next() == "D"
        flatIterator.hasNext()
        flatIterator.next() == "E"
        flatIterator.hasNext()
        flatIterator.next() == "F"
        flatIterator.hasNext()
        flatIterator.next() == "G"
        flatIterator.hasNext()
        flatIterator.next() == "H"
        !flatIterator.hasNext()
    }

    private Iterator generatePageIterator(List<List<String>> pages) {
        new Iterator() {
            private currentIndex = 0;

            @Override
            boolean hasNext() {
                return currentIndex < pages.size();
            }

            @Override
            List<String> next() {
                return pages.get(currentIndex++);
            }
        }
    }
}
