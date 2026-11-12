package com.boclips.kalturaclient

import com.boclips.kalturaclient.bulkupload.BulkUpload
import com.boclips.kalturaclient.bulkupload.BulkUploadStatus
import com.boclips.kalturaclient.clients.TestKalturaClient
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
        def entries = client.getEntries().toList()
        entries.isEmpty()
    }

    def "a bulk delete removes the entries and finishes straight away"() {
        given:
        def client = new TestKalturaClient()
        ["1", "2", "3"].each { client.addMediaEntry(MediaEntry.builder().id(it).referenceId(it).build()) }

        when:
        BulkUpload bulkUpload = client.deleteEntriesInBulk(["1", "2"])

        then:
        client.getEntries().toList()*.id == ["3"]
        bulkUpload.status == BulkUploadStatus.FINISHED
        client.getBulkUpload(bulkUpload.id) == bulkUpload
    }

    def "a bulk delete fails when none of the entries exist"() {
        given:
        def client = new TestKalturaClient()

        expect:
        client.deleteEntriesInBulk(["1", "2"]).status == BulkUploadStatus.FAILED
    }

    def "a bulk delete finishes partially when some of the entries do not exist"() {
        given:
        def client = new TestKalturaClient()
        client.addMediaEntry(MediaEntry.builder().id("1").referenceId("1").build())

        when:
        BulkUpload bulkUpload = client.deleteEntriesInBulk(["1", "2"])

        then:
        bulkUpload.status == BulkUploadStatus.FINISHED_PARTIALLY
        client.getEntries().toList().isEmpty()
    }

    def "each bulk delete gets its own id"() {
        given:
        def client = new TestKalturaClient()

        expect:
        client.deleteEntriesInBulk(["1"]).id != client.deleteEntriesInBulk(["2"]).id
    }

    def "a bulk upload's status can be set"() {
        given:
        def client = new TestKalturaClient()
        def id = client.deleteEntriesInBulk(["1"]).id

        when:
        client.setBulkUploadStatus(id, BulkUploadStatus.PROCESSING)

        then:
        client.getBulkUpload(id).status == BulkUploadStatus.PROCESSING
    }

    def "clear removes bulk uploads"() {
        given:
        def client = new TestKalturaClient()
        def id = client.deleteEntriesInBulk(["1"]).id

        when:
        client.clear()

        then:
        client.getBulkUpload(id) == null
    }
}
