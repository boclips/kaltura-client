package com.boclips.kalturaclient

import com.boclips.kalturaclient.media.MediaEntry
import spock.lang.Specification

class TestKalturaClientTest extends Specification {


    def "clear removes all entries"() {
        given:
        def client = new TestKalturaClient()
        client.addMediaEntry(MediaEntry.builder().id("1").referenceId("1").build())

        when:
        client.clear()

        then:
        List<MediaEntry> entries = client.getMediaEntriesByReferenceId("1")
        entries.isEmpty()
    }
}
