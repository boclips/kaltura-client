package com.boclips.kalturaclient.flavorAsset

import com.boclips.kalturaclient.KalturaClient
import com.boclips.kalturaclient.config.KalturaClientConfig
import com.boclips.kalturaclient.http.KalturaRestClient
import com.boclips.kalturaclient.media.links.GenerateKalturaSessionException
import com.boclips.kalturaclient.media.links.StreamUrlSessionGenerator
import com.boclips.kalturaclient.media.streams.StreamFormat
import com.boclips.kalturaclient.testsupport.TestFactories
import spock.lang.Specification

class FlavorAssetGetDownloadUrlClientTest extends Specification {

    private KalturaRestClient client
    private FlavorAssetGetDownloadUrlClient flavorAssetGetDownloadUrlClient
    private StreamUrlSessionGenerator sessionGenerator

    def "setup"() {
        client = Mock(KalturaRestClient) {
            getConfig() >> KalturaClientConfig.builder()
                    .partnerId(123)
                    .userId("user")
                    .secret("secret")
                    .captionProviderHostname("hostname.com")
                    .captionProviderApiKey("api-key")
                    .build()
        }
        sessionGenerator = Mock(StreamUrlSessionGenerator)

        flavorAssetGetDownloadUrlClient = new FlavorAssetGetDownloadUrlClient(client, sessionGenerator)
    }

    def "can get asset url with kaltura session"() {
        given:
        String assetId = "asset-id"
        sessionGenerator.getForEntry(assetId) >> "added-kaltura-session"
        client.get("/flavorasset/action/getUrl", _ as HashMap, URI.class) >> new URI("https://cfvod.kaltura.com/pd/p/1776261/sp/177626100/serveFlavor/entryId/0_7y40akwx/v/11/ev/1/flavorId/asset-id/fileName/file-name.mp4/name/a.mp4")

        when:
        String downloadUrl = flavorAssetGetDownloadUrlClient.getDownloadUrl(assetId, true)

        then:
        downloadUrl.equals("https://cfvod.kaltura.com/pd/p/1776261/sp/177626100/serveFlavor/entryId/0_7y40akwx/v/11/ev/1/flavorId/asset-id/fileName/file-name.mp4/ks/added-kaltura-session/name/a.mp4")
    }

    def "can get asset url without kaltura session"() {
        given:
        String assetId = "asset-id"
        client.get("/flavorasset/action/getUrl", _ as HashMap, URI.class) >> new URI("https://cfvod.kaltura.com/pd/p/1776261/sp/177626100/serveFlavor/entryId/0_7y40akwx/v/11/ev/1/flavorId/asset-id/fileName/file-name.mp4/a.mp4")


        when:
        String downloadUrl = flavorAssetGetDownloadUrlClient.getDownloadUrl(assetId, false)

        then:
        downloadUrl.equals("https://cfvod.kaltura.com/pd/p/1776261/sp/177626100/serveFlavor/entryId/0_7y40akwx/v/11/ev/1/flavorId/asset-id/fileName/file-name.mp4/a.mp4")
    }

    def "throws KalturaSessionException when generating session fails"() {
        given:
        String entryId = "media-entry-id"
        client.get("/flavorasset/action/getUrl", _ as HashMap, URI.class) >> new URI("https://cfvod.kaltura.com/pd/p/1776261/sp/177626100/serveFlavor/entryId/0_7y40akwx/v/11/ev/1/flavorId/asset-id/fileName/file-name.mp4/a.mp4")
        sessionGenerator.getForEntry(entryId) >> { throw new RuntimeException("something went wrong") }

        when:
        flavorAssetGetDownloadUrlClient.getDownloadUrl(entryId, true)

        then:
        thrown GenerateKalturaSessionException
    }
}