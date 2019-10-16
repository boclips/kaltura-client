package com.boclips.kalturaclient.http

import com.boclips.kalturaclient.session.KalturaSession
import com.boclips.kalturaclient.session.SessionGenerator
import spock.lang.Specification

class KalturaRestClientTest extends Specification {
    private HttpClient httpClient
    private SessionGenerator sessionGenerator
    private KalturaRestClient client

    def setup() {
        httpClient = Mock(HttpClient)

        sessionGenerator = Mock(SessionGenerator) {
            get() >> Mock(KalturaSession) {
                getToken() >> "TEST_TOKEN"
            }
        }

        client = new KalturaRestClient(this.httpClient,"https://my.base.url/path", this.sessionGenerator)
    }

    def "it makes a get request to the right endpoint"() {
        when:
        def result = client.get("/request", [headerOne: "valueOne", headerTwo: "valueTwo"], String.class)

        then:
        1 * httpClient.get("https://my.base.url/path/request", _, String.class) >> "Hello World"

        result == "Hello World"
    }

    def "it adds a session token query parameter on a get"() {
        when:
        def result = client.get("/request", [headerOne: "valueOne", headerTwo: "valueTwo"], String.class)

        then:
        1 * httpClient.get(_, { it.get("ks") == "TEST_TOKEN" }, String.class) >> "Hello World"

        result == "Hello World"
    }

    def "it adds a format query parameter on a get"() {
        when:
        def result = client.get("/", [:], String.class)

        then:
        1 * httpClient.get(_, { it.get("format") == "1" }, String.class) >> "Hello World"

        result == "Hello World"
    }

    def "passes the multipart data through on a post"() {
        when:
        def result = client.post("/", [:], [dataOne: "valueOne"], String.class)

        then:
        1 * httpClient.post(_, _, [dataOne: "valueOne"], String.class) >> "Hello World"

        result == "Hello World"
    }

    def "passes the query parameters through on a post"() {
        when:
        def result = client.post("/", [postQuery: "valuePost"], String.class)

        then:
        1 * httpClient.post(_, { it.get("postQuery") == "valuePost" }, _, String.class) >> "Hello World"

        result == "Hello World"
    }

    def "defaults empty multipart data when not provided on a post"() {
        when:
        def result = client.post("/", [:], String.class)

        then:
        1 * httpClient.post(_, _, [:], String.class) >> "Hello World"

        result == "Hello World"
    }

    def "it adds a session token query parameter on a post"() {
        when:
        def result = client.post("/request", [headerOne: "valueOne", headerTwo: "valueTwo"], String.class)

        then:
        1 * httpClient.post(_, { it.get("ks") == "TEST_TOKEN" }, _, String.class) >> "Hello World"

        result == "Hello World"
    }
}
