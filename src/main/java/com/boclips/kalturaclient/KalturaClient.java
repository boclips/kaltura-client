package com.boclips.kalturaclient;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class KalturaClient {
    private KalturaClientConfig config;

    public KalturaClient(KalturaClientConfig config) {
        this.config = config;
    }

    public KalturaSession generateSession(int ttlSeconds) {
        try {
            String token = Unirest.post(this.config.getBaseUrl() + "/api_v3/service/session/action/start")
                    .field("expiry", ttlSeconds)
                    .field("format", "1")
                    .field("partnerId", config.getPartnerId())
                    .field("secret", config.getSecret())
                    .field("type", "0")
                    .field("userId", config.getUserId())
                    .asString()
                    .getBody();

            return new KalturaSession(tokenWithoutQuotes(token));
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    private String tokenWithoutQuotes(String token) {
        return token.substring(1, token.length() - 1);
    }
}
