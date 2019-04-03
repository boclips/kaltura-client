package com.boclips.kalturaclient.http;

import com.boclips.kalturaclient.session.SessionGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
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
        try {
            return Unirest.get(this.baseUrl + "/api_v3" + path)
                    .queryString("ks", sessionGenerator.get().getToken())
                    .queryString("format", "1")
                    .queryString(queryParams)
                    .asObject(responseType)
                    .getBody();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T post(String path, Map<String, Object> queryParams, Class<T> responseType) {
        try {
            return Unirest.post(this.baseUrl + "/api_v3" + path)
                    .queryString("ks", sessionGenerator.get().getToken())
                    .queryString("format", "1")
                    .queryString(queryParams)
                    .asObject(responseType)
                    .getBody();
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
