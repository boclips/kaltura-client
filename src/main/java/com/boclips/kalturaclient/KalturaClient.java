package com.boclips.kalturaclient;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class KalturaClient {
    private String baseApiUrl;

    public KalturaClient(String baseApiUrl) {
        this.baseApiUrl = baseApiUrl;
    }

    public KalturaSession generateSession() {
        try {
            System.out.println(Unirest.post(this.baseApiUrl + "/api_v3/service/session/action/start")
                    .field("expiry", "86400")
                    .field("format", "1")
                    .field("partnerId", "abc")
                    .field("secret", "123")
                    .field("type", "0")
                    .field("userId", "user@kaltura.com")
                    .asString());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return new KalturaSession();
    }
}
