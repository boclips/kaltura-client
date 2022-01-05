package com.boclips.kalturaclient.captionsProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

public class HttpTypedClient {

    public <T> T execute(URL request, Class<T> responseType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(request, responseType);
    }
}
