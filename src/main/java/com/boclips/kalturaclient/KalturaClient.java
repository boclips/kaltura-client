package com.boclips.kalturaclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.List;

public class KalturaClient {
    private KalturaClientConfig config;
    private KalturaSession session;

    public KalturaClient(KalturaClientConfig config) {
        this.config = config;

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

    public List<MediaEntry> mediaEntriesByReferenceIds(String... referenceIds) {
        try {
            MediaList mediaList = Unirest.get(this.config.getBaseUrl() + "/api_v3/service/media/action/list")
                    .queryString("ks", this.session.getToken())
                    .queryString("filter[referenceIdIn]", String.join(",", referenceIds))
                    .queryString("format", "1")
                    .asObject(MediaList.class)
                    .getBody();

            return mediaList.objects;

        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }

    }
}
