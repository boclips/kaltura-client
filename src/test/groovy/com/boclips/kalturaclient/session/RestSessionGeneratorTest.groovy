package com.boclips.kalturaclient.session

import spock.lang.Specification

import java.time.Instant

class RestSessionGeneratorTest extends Specification {
    def "gets the first kaltura session"() {
        given:
        SessionRetriever sessionRetriever = Mock(SessionRetriever)
        sessionRetriever.fetch() >> "some-session"

        when:
        RestSessionGenerator generator = new RestSessionGenerator(sessionRetriever, 1000)
        KalturaSession session = generator.get()

        then:
        session.token == 'some-session'
        session.expires > Instant.now()
    }

    def "uses cached session as long as within ttl"() {
        given:
        SessionRetriever sessionRetriever = Mock(SessionRetriever)
        SessionGenerator generator = new RestSessionGenerator(sessionRetriever, 1000)

        when:
        generator.get()
        generator.get()
        generator.get()

        then:
        1 * sessionRetriever.fetch() >> "some-crap"
    }

    def "renews session when expiry is within 5 second"() {
        given:
        SessionRetriever sessionRetriever = Mock(SessionRetriever)
        SessionGenerator generator = new RestSessionGenerator(sessionRetriever, 5)

        when:
        generator.get()
        Thread.sleep(100)
        generator.get()

        then:
        2 * sessionRetriever.fetch() >> "some-crap"
    }
}
