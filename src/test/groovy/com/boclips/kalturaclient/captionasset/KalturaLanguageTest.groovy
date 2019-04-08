package com.boclips.kalturaclient.captionasset

import spock.lang.Specification

class KalturaLanguageTest extends Specification {
    def "fromName with an existing name"() {
        expect:
        KalturaLanguage.fromName("English") == KalturaLanguage.ENGLISH
        KalturaLanguage.fromName("Spanish") == KalturaLanguage.SPANISH
    }

    def "fromName with a non-existing name"() {
        when:
        KalturaLanguage.fromName("not a valid language")

        then:
        thrown(IllegalArgumentException.class)
    }
}
