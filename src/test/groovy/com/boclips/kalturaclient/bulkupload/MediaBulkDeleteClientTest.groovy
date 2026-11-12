package com.boclips.kalturaclient.bulkupload

import com.boclips.kalturaclient.bulkupload.resources.BulkUploadResource
import com.boclips.kalturaclient.http.KalturaClientApiException
import com.boclips.kalturaclient.http.KalturaRestClient
import com.boclips.kalturaclient.http.UploadFileDescriptor
import kong.unirest.ContentType
import spock.lang.Specification

class MediaBulkDeleteClientTest extends Specification {
    KalturaRestClient restClient = Mock(KalturaRestClient)
    MediaBulkDeleteClient client = new MediaBulkDeleteClient(restClient)

    def "submits the entries as an XML bulk upload of delete actions"() {
        given:
        UploadFileDescriptor upload = null
        Map<String, Object> params = null

        when:
        BulkUpload bulkUpload = client.deleteEntries(["0_abc", "1_def"])

        then:
        1 * restClient.postFileBody("/media/action/bulkUploadAdd", _, _, BulkUploadResource.class) >> { path, query, file, type ->
            params = query
            upload = file
            new BulkUploadResource(objectType: "KalturaBulkUpload", id: 123, status: 0)
        }

        params == [
                "bulkUploadData:objectType"     : "KalturaBulkUploadXmlJobData",
                "bulkUploadEntryData:objectType": "KalturaBulkUploadEntryData",
        ]
        upload.fieldName == "fileData"
        upload.contentType == ContentType.TEXT_XML
        def items = new XmlSlurper().parse(upload.fileStream).channel.item
        items.collect { [it.action.text(), it.entryId.text()] } == [["delete", "0_abc"], ["delete", "1_def"]]

        bulkUpload == new BulkUpload("123", BulkUploadStatus.PENDING, null)
    }

    def "throws when Kaltura rejects the bulk upload"() {
        given:
        restClient.postFileBody(_, _, _, _) >> new BulkUploadResource(
                objectType: "KalturaAPIException", code: "SERVICE_FORBIDDEN", message: "not allowed")

        when:
        client.deleteEntries(["0_abc"])

        then:
        def e = thrown(KalturaClientApiException)
        e.message.contains("SERVICE_FORBIDDEN")
        e.message.contains("not allowed")
    }
}
