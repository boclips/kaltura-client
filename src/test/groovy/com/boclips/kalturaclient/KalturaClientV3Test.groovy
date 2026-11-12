package com.boclips.kalturaclient

import com.boclips.kalturaclient.bulkupload.BulkUpload
import com.boclips.kalturaclient.bulkupload.BulkUploadStatus
import com.boclips.kalturaclient.bulkupload.resources.BulkUploadResource
import com.boclips.kalturaclient.clients.KalturaClientV3
import com.boclips.kalturaclient.config.KalturaClientConfig
import com.boclips.kalturaclient.flavorParams.resources.FlavorParamsListResource
import com.boclips.kalturaclient.http.KalturaRestClient
import com.boclips.kalturaclient.media.resources.MediaListResource
import spock.lang.Specification

import static com.boclips.kalturaclient.testsupport.TestFactories.FlavorParamsListResourceFactory

class KalturaClientV3Test extends Specification {
    KalturaRestClient restClient = Mock(KalturaRestClient) {
        get("/flavorparams/action/list", _, _) >> FlavorParamsListResourceFactory.sample()
        get("/media/action/list", _, _) >> new MediaListResource(objects: [], objectType: "KalturaMediaListResponse")
    }
    KalturaClientConfig config = KalturaClientConfig.builder()
            .partnerId(123)
            .userId("user-id")
            .secret("secret")
            .captionProviderHostname("hostname.com")
            .captionProviderApiKey("api-key")
            .build()

    def "it loads the project flavorParams on instantiation"() {
        given:
        FlavorParamsListResource flavorParamsListResource = FlavorParamsListResourceFactory.sample()

        when:
        KalturaClientV3 kalturaClient = new KalturaClientV3(restClient, config, null)

        then:
        1 * restClient.get("/flavorparams/action/list", _, _) >> flavorParamsListResource

        kalturaClient.getFlavorParams().get(0).getId() == flavorParamsListResource.objects.get(0).id
        kalturaClient.getFlavorParams().get(0).getHeight() == flavorParamsListResource.objects.get(0).height
        kalturaClient.getFlavorParams().get(0).getWidth() == flavorParamsListResource.objects.get(0).width
    }

    def "gets entries by id in one page large enough to hold them all"() {
        given:
        def ids = (1..40).collect { "0_entry$it" }
        def kalturaClient = new KalturaClientV3(restClient, config, null)

        when:
        kalturaClient.getEntries(ids)

        then:
        1 * restClient.get("/media/action/list", { it["pager[pageSize]"] == "40" && it["filter[idIn]"] == ids.join(",") }, MediaListResource.class) >>
                new MediaListResource(objects: [], objectType: "KalturaMediaListResponse")
    }

    def "gets up to 500 entries by id"() {
        given:
        def kalturaClient = new KalturaClientV3(restClient, config, null)

        when:
        kalturaClient.getEntries((1..500).collect { "0_entry$it" })

        then:
        1 * restClient.get("/media/action/list", { it["pager[pageSize]"] == "500" }, MediaListResource.class) >>
                new MediaListResource(objects: [], objectType: "KalturaMediaListResponse")
    }

    def "refuses more entry ids than one page can hold"() {
        given:
        def kalturaClient = new KalturaClientV3(restClient, config, null)

        when:
        kalturaClient.getEntries((1..501).collect { "0_entry$it" })

        then:
        thrown(IllegalArgumentException)
    }

    def "deletes entries in bulk"() {
        given:
        def kalturaClient = new KalturaClientV3(restClient, config, null)

        when:
        BulkUpload bulkUpload = kalturaClient.deleteEntriesInBulk(["0_entry1"])

        then:
        1 * restClient.postFileBody("/media/action/bulkUploadAdd", _, _, BulkUploadResource.class) >>
                new BulkUploadResource(objectType: "KalturaBulkUpload", id: 7, status: 1)
        bulkUpload == new BulkUpload("7", BulkUploadStatus.QUEUED, null)
    }

    def "gets a bulk upload"() {
        given:
        def kalturaClient = new KalturaClientV3(restClient, config, null)

        when:
        BulkUpload bulkUpload = kalturaClient.getBulkUpload("7")

        then:
        1 * restClient.get("/bulkupload_bulk/action/get", [id: "7"], BulkUploadResource.class) >>
                new BulkUploadResource(objectType: "KalturaBulkUpload", id: 7, status: 5)
        bulkUpload == new BulkUpload("7", BulkUploadStatus.FINISHED, null)
    }
}
