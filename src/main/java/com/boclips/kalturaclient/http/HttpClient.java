package com.boclips.kalturaclient.http;

import com.boclips.kalturaclient.session.SessionGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Slf4j
public class HttpClient {
    private final String baseUrl;
    private final SessionGenerator sessionGenerator;

    public HttpClient(String baseUrl, SessionGenerator sessionGenerator) {
        this.baseUrl = baseUrl;
        this.sessionGenerator = sessionGenerator;

        configureUniRest();
    }

    public <T> T get(String path, Map<String, Object> queryParams, Class<T> responseType) {
        return makeRequest(Unirest.get(this.baseUrl + path), queryParams, responseType);
    }

    public <T> T post(String path, Map<String, Object> queryParams, Class<T> responseType) {
        return post(path, queryParams, Collections.emptyMap(), responseType);
    }

    public <T> T post(String path, Map<String, Object> queryParams, Map<String, Object> multipartParams, Class<T> responseType) {
        HttpRequestWithBody post = Unirest.post(this.baseUrl + path);
        multipartParams.forEach(post::field);
        return makeRequest(post, queryParams, responseType);
    }

    private <T> T makeRequest(HttpRequest req, Map<String, Object> queryParams, Class<T> responseType) {
        try {
            HttpResponse<T> response = req
                    .queryString("ks", sessionGenerator.get().getToken())
                    .queryString("format", "1")
                    .queryString(queryParams)
                    .asObject(responseType);

            if(response.getStatus() >= 400) {
                throw new RuntimeException(req.getHttpMethod().name() + " request to " + req.getUrl() + " failed with status " + response.getStatus());
            }

            return response.getBody();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    private void configureUniRest() {
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            {
                jacksonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            }

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
