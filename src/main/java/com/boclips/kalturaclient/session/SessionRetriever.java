package com.boclips.kalturaclient.session;

import com.boclips.kalturaclient.config.KalturaClientConfig;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SessionRetriever {
    private KalturaClientConfig config;

    public String fetch() {
        try {
            String response = Unirest.post(this.config.getBaseUrl() + "/api_v3/service/session/action/start")
                    .field("expiry", this.config.getSessionTtl())
                    .field("format", "1")
                    .field("partnerId", this.config.getPartnerId())
                    .field("secret", this.config.getSecret())
                    .field("type", "2")
                    .field("userId", this.config.getUserId())
                    .asString()
                    .getBody();
            return tokenWithoutQuotes(response);
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    private String tokenWithoutQuotes(String token) {
        return token.substring(1, token.length() - 1);
    }
}
