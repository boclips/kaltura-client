package com.boclips.kalturaclient

import com.boclips.kalturaclient.KalturaClientConfig
import com.boclips.kalturaclient.KalturaClientV3
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

    def "toMapByReferenceIdIgnoringDuplicates ignores duplicates"() {
        given:
        def mediaEntries = [
                MediaEntry.builder().id("id-1").referenceId("refId").build(),
                MediaEntry.builder().id("id-2").referenceId("refId").build()
        ]

        when:
        def map = KalturaClientV3.toMapByReferenceIdIgnoringDuplicates(mediaEntries)

        then:
        assert map.size() == 1
    }

}
