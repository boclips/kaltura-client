package com.boclips.kalturaclient.http;

import com.boclips.kalturaclient.media.resources.MediaListResource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;

public class HttpClient {
    private String baseUrl;

    public HttpClient(String baseUrl) {
        this.baseUrl = baseUrl;

        configureUniRest();
    }

    public MediaListResource getMediaListResource(String sessionToken, RequestFilters filters) {
        try {
            MediaListResource response = Unirest.get(this.baseUrl + "/api_v3/service/media/action/list")
                    .queryString(filters.toMap())
                    .queryString("ks", sessionToken)
                    .queryString("format", "1")
                    .asObject(MediaListResource.class)
                    .getBody();

            if (!ResponseObjectType.isSuccessful(response.objectType)) {
                throw new UnsupportedOperationException(String.format("Error in Kaltura request: %s", response.code));
            }

            return response;
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
