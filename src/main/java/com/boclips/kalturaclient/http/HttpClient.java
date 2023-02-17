package com.boclips.kalturaclient.http;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.ObjectMapper;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.GetRequest;
import kong.unirest.HttpRequest;
import kong.unirest.HttpRequestWithBody;

public class HttpClient {

    public HttpClient() {
        Unirest.config().setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            {
                jacksonObjectMapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
                jacksonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            }

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public <T> T readValue(String value, GenericType<T> genericType) {
                try {
                    return jacksonObjectMapper.readValue(value, jacksonObjectMapper.constructType(genericType.getType()));
                } catch (IOException e) {
                    throw new UnirestException(e);
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

    public <T> T get(String path, Map<String, Object> queryParams, Class<T> responseType) {
        GetRequest getRequest = Unirest.get(path);

        return makeRequest(getRequest, queryParams, responseType);
    }

    public <T> T post(String path, Map<String, Object> queryParams, Class<T> responseType) {
        return post(path, queryParams, Collections.emptyMap(), responseType);
    }

    public <T> T post(String path, Map<String, Object> queryParams, Map<String, Object> multipartParams, Class<T> responseType) {
        HttpRequestWithBody post = Unirest.post(path);
        multipartParams.forEach(post::field);

        return makeRequest(post, queryParams, responseType);
    }

    public <T> T postImage(String path, Map<String, Object> queryParams, UploadFileDescriptor fileDescriptor, Class<T> responseType) {
        HttpRequestWithBody post = Unirest.post(path);
        post.field(fileDescriptor.getFieldName(), fileDescriptor.getFileStream(),
                fileDescriptor.getContentType(), fileDescriptor.getFilename());

        return makeRequest(post, queryParams, responseType);
    }

    private <T> T makeRequest(HttpRequest<?> request, Map<String, Object> queryParams, Class<T> responseType) {
        try {
            HttpRequest<?> requestWithQuery = request.queryString(queryParams);
            HttpResponse<T> response = requestWithQuery.asObject(responseType);

            if (response.getStatus() >= 400) {
                throw new RuntimeException(request.getHttpMethod().name() + " request to " + request.getUrl() + " failed with status " + response.getStatus());
            }

            return response.getBody();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

}
