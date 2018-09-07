package com.boclips.kalturaclient;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class KalturaClient {
    public KalturaClient() {
    }

    public KalturaSession generateSession() {
        try {
            System.out.println(Unirest.post("http://localhost:9999/api_v3/service/session/action/start").asString());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return new KalturaSession();
    }
}
