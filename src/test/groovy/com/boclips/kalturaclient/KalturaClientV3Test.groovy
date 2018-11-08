package com.boclips.kalturaclient


import com.boclips.kalturaclient.media.MediaEntry
import com.boclips.kalturaclient.session.SessionGenerator
import spock.lang.Specification

class KalturaClientV3Test extends Specification {

    def "accepts a valid configuration"() {
        when:
        KalturaClientConfig config = KalturaClientConfig.builder()
                .baseUrl("common://www.kaltura.com")
                .userId("1")
                .secret("123")
                .partnerId("999")
                .build()

        then:
        new KalturaClientV3(config, Mock(SessionGenerator))
    }

    def "throws when userId not set"() {
        when:
        KalturaClientConfig config = KalturaClientConfig.builder()
                .baseUrl("common://www.kaltura.com")
                .secret("123")
                .partnerId("999")
                .build()
        new KalturaClientV3(config, Mock(SessionGenerator))

        then:
        thrown Exception
    }

    def "throws when secret not set"() {
        when:
        KalturaClientConfig config = KalturaClientConfig.builder()
                .baseUrl("common://www.kaltura.com")
                .userId("1")
                .partnerId("999")
                .build()
        new KalturaClientV3(config, Mock(SessionGenerator))

        then:
        thrown Exception
    }

    def "throws when partnerId not set"() {
        when:
        KalturaClientConfig config = KalturaClientConfig.builder()
                .baseUrl("common://www.kaltura.com")
                .userId("1")
                .secret("123")
                .build()
        new KalturaClientV3(config, Mock(SessionGenerator))

        then:
        thrown Exception
    }

    def "getMediaEntriesByReferenceIds does not call Kaltura when list of ids empty"() {
        given:
        KalturaClientConfig config = KalturaClientConfig.builder()
                .baseUrl("common://www.kaltura.com")
                .partnerId("not a real partner id")
                .userId("not a real user id")
                .secret("not a real secret")
                .build()
        KalturaClientV3 client = new KalturaClientV3(config, Mock(SessionGenerator))

        when:
        Map<String, List<MediaEntry>> entries = client.getMediaEntriesByReferenceIds([])

        then:
        entries.isEmpty()
    }

}
