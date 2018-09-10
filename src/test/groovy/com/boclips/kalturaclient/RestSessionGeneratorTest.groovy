package com.boclips.kalturaclient

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

    def "renews session when it has expired"() {
        given:
        SessionRetriever sessionRetriever = Mock(SessionRetriever)
        SessionGenerator generator = new RestSessionGenerator(sessionRetriever, 0)

        when:
        generator.get()
        Thread.sleep(1000)
        generator.get()

        then:
        2 * sessionRetriever.fetch() >> "some-crap"
    }
}
