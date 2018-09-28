package com.boclips.kalturaclient.http;

import com.boclips.kalturaclient.media.resources.MediaListResource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
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
                    .queryString("filter[statusIn]", "-2,-1,0,1,2,4,5,6,7")
                    .asObject(MediaListResource.class)
                    .getBody();

            log.debug("Get media list of size {} response: {}", response.totalCount, response);

            if (!ResponseObjectType.isSuccessful(response.objectType)) {
                throw new UnsupportedOperationException(String.format("Error in Kaltura request: %s", response.code));
            }

            return response;
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    public void addMediaEntry(String sessionToken, String referenceId) {
        try {
            final HttpResponse<String> response = Unirest.post(this.baseUrl + "/api_v3/service/media/action/add")
                    .queryString("ks", sessionToken)
                    .queryString("format", "1")
                    .queryString("entry[mediaType]", 1)
                    .queryString("entry[objectType]", "KalturaMediaEntry")
                    .queryString("entry[referenceId]", referenceId)
                    .asString();
            log.debug("media.add response: {}", response);
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    public void deleteMediaEntryByReferenceId(String sessionToken, String referenceId) {
        try {
            final HttpResponse<String> response = Unirest.post(this.baseUrl + "/api_v3/service/media/action/delete")
                    .queryString("format", "1")
                    .queryString("ks", sessionToken)
                    .queryString("entryId", referenceId)
                    .asString();
            log.debug("media.delete response: {} {}", response.getStatus(), response.getBody());
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
