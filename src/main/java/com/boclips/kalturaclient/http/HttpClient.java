package com.boclips.kalturaclient.http;

import com.boclips.kalturaclient.captionasset.CaptionAsset;
import com.boclips.kalturaclient.captionasset.CaptionAssetList;
import com.boclips.kalturaclient.captionasset.resources.CaptionAssetListResource;
import com.boclips.kalturaclient.captionasset.resources.CaptionAssetResource;
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

    public MediaListResource listMediaEntries(String sessionToken, RequestFilters filters) {
        try {
            HttpResponse<MediaListResource> response = Unirest.get(this.baseUrl + "/api_v3/service/media/action/list")
                    .queryString(filters.toMap())
                    .queryString("ks", sessionToken)
                    .queryString("format", "1")
                    .queryString("filter[statusIn]", "-2,-1,0,1,2,4,5,6,7")
                    .asObject(MediaListResource.class);

            log.debug("/action/list returned: {} with body {}", response.getStatus(), response.getBody());

            MediaListResource mediaListResource = response.getBody();
            if (!ResponseObjectType.isSuccessful(mediaListResource.objectType)) {
                throw new UnsupportedOperationException(String.format("Error in Kaltura request: %s", mediaListResource.code));
            }

            return mediaListResource;
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

            log.debug("/action/add returned: {} with body {}", response.getStatus(), response.getBody());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    public void deleteMediaEntryByEntityId(String sessionToken, String entityId) {
        try {
            final HttpResponse<String> response = Unirest.post(this.baseUrl + "/api_v3/service/media/action/delete")
                    .queryString("ks", sessionToken)
                    .queryString("format", "1")
                    .queryString("entryId", entityId)
                    .asString();

            log.debug("/action/delete returned: {} with body {}", response.getStatus(), response.getBody());

            if (response.getBody().contains("KalturaAPIException")) {
                throw new KalturaClientApiException(
                        String.format("Media Entry %s was not deleted, API returned %s",
                                entityId,
                                response.getBody())
                );
            }
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    public CaptionAssetResource addCaptionAsset(String sessionToken, String entryId, CaptionAsset captionAsset) {
        try {
            return Unirest.post(this.baseUrl + "/api_v3/service/caption_captionasset/action/add")
                    .queryString("ks", sessionToken)
                    .queryString("format", "1")
                    .queryString("entryId", entryId)
                    .queryString("captionAsset[format]", captionAsset.getFileType().getValue())
                    .queryString("captionAsset[language]", captionAsset.getLanguage())
                    .queryString("captionAsset[label]", captionAsset.getLabel())
                    .asObject(CaptionAssetResource.class)
                    .getBody();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    public CaptionAssetListResource listCaptionAssets(String sessionToken, RequestFilters filters) {
        try {
            return Unirest.get(this.baseUrl + "/api_v3/service/caption_captionasset/action/list")
                    .queryString(filters.toMap())
                    .queryString("ks", sessionToken)
                    .queryString("format", "1")
                    .asObject(CaptionAssetListResource.class)
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

    public void setCaptionAssetContent(String sessionToken, String captionAssetId, String content) {
        try {
            Unirest.post(this.baseUrl + "/api_v3/service/caption_captionasset/action/setContent")
                    .queryString("ks", sessionToken)
                    .queryString("id", captionAssetId)
                    .queryString("contentResource[objectType]", "KalturaStringResource")
                    .queryString("contentResource[content]", content)
                    .asObject(String.class)
                    .getBody();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    public String serveCaptionAsset(String sessionToken, String assetId) {
        try {
            return Unirest.get(this.baseUrl + "/api_v3/service/caption_captionasset/action/serve")
                    .queryString("ks", sessionToken)
                    .queryString("captionAssetId", assetId)
                    .asObject(String.class)
                    .getBody();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }
}
