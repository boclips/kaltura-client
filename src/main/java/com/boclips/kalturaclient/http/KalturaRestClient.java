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
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class KalturaRestClient {
    private final HttpClient httpClient;
    private final String baseUrl;
    private final SessionGenerator sessionGenerator;

    public static KalturaRestClient create(String baseUrl, SessionGenerator sessionGenerator) {
        return new KalturaRestClient(new HttpClient(), baseUrl, sessionGenerator);
    }

    public KalturaRestClient(HttpClient httpClient, String baseUrl, SessionGenerator sessionGenerator) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
        this.sessionGenerator = sessionGenerator;
    }

    public <T> T get(String path, Map<String, Object> queryParams, Class<T> responseType) {
        return httpClient.get(this.baseUrl + path, appendQueryParameters(queryParams), responseType);
    }

    public <T> T post(String path, Map<String, Object> queryParams, Class<T> responseType) {
        return post(path, queryParams, Collections.emptyMap(), responseType);
    }

    public <T> T post(String path, Map<String, Object> queryParams, Map<String, Object> multipartParams, Class<T> responseType) {
        return httpClient.post(this.baseUrl + path, appendQueryParameters(queryParams), multipartParams, responseType);
    }

    private Map<String, Object> appendQueryParameters(final Map<String, Object> queryParams) {
        Map<String, Object> parameters = new HashMap<>(queryParams);

        parameters.put("ks", sessionGenerator.get().getToken());
        parameters.put("format", "1");

        return parameters;
    }

}
