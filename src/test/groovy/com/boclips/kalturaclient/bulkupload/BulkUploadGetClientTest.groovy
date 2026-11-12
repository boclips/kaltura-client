package com.boclips.kalturaclient.bulkupload

import com.boclips.kalturaclient.bulkupload.resources.BulkUploadResource
import com.boclips.kalturaclient.http.KalturaClientApiException
import com.boclips.kalturaclient.http.KalturaRestClient
import spock.lang.Specification

class BulkUploadGetClientTest extends Specification {
    KalturaRestClient restClient = Mock(KalturaRestClient)
    BulkUploadGetClient client = new BulkUploadGetClient(restClient)

    def "gets a bulk upload by id"() {
        when:
        BulkUpload bulkUpload = client.get("123")

        then:
        1 * restClient.get("/bulkupload_bulk/action/get", [id: "123"], BulkUploadResource.class) >>
                new BulkUploadResource(objectType: "KalturaBulkUpload", id: 123, status: 12, error: "2 items failed")

        bulkUpload == new BulkUpload("123", BulkUploadStatus.FINISHED_PARTIALLY, "2 items failed")
    }

    def "throws when Kaltura returns an error"() {
        given:
        restClient.get(_, _, _) >> new BulkUploadResource(
                objectType: "KalturaAPIException", code: "BULK_UPLOAD_NOT_FOUND", message: "no such bulk upload")

        when:
        client.get("123")

        then:
        def e = thrown(KalturaClientApiException)
        e.message.contains("BULK_UPLOAD_NOT_FOUND")
    }
}
