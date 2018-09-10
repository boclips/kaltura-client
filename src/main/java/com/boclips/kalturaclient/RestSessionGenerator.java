package com.boclips.kalturaclient;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class RestSessionGenerator implements SessionGenerator {
    private KalturaClientConfig config;

    public RestSessionGenerator(KalturaClientConfig config) {
        this.config = config;
    }

    public KalturaSession get() {
        try {
            String token = Unirest.post(this.config.getBaseUrl() + "/api_v3/service/session/action/start")
                    .field("expiry", this.config.getSessionTtl())
                    .field("format", "1")
                    .field("partnerId", this.config.getPartnerId())
                    .field("secret", this.config.getSecret())
                    .field("type", "0")
                    .field("userId", this.config.getUserId())
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
