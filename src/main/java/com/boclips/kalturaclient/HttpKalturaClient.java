package com.boclips.kalturaclient;

import com.boclips.kalturaclient.session.SessionGenerator;
import com.boclips.kalturaclient.streams.StreamUrlProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpKalturaClient implements KalturaClient {
    private KalturaClientConfig config;
    private SessionGenerator sessionGenerator;

    public HttpKalturaClient(KalturaClientConfig config, SessionGenerator sessionGenerator) {
        this.config = config;
        this.sessionGenerator = sessionGenerator;
        configureUniRest();
    }

    @Override
    public Map<String, MediaEntry> mediaEntriesByReferenceIds(String... referenceIds) {
        try {
            MediaListResource mediaListResource = Unirest.get(this.config.getBaseUrl() + "/api_v3/service/media/action/list")
                    .queryString("ks", this.sessionGenerator.get().getToken())
                    .queryString("filter[referenceIdIn]", String.join(",", referenceIds))
                    .queryString("format", "1")
                    .asObject(MediaListResource.class)
                    .getBody();

            StreamUrlProducer streamUrlProducer = new StreamUrlProducer(config);
            return mediaListResource.objects.stream().map(mediaEntryResource -> MediaEntry.builder()
                    .id(mediaEntryResource.getId())
                    .referenceId(mediaEntryResource.getReferenceId())
                    .streams(streamUrlProducer.convert(mediaEntryResource))
                    .build()).collect(Collectors.toMap(MediaEntry::getReferenceId, mediaEntry -> mediaEntry));

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
