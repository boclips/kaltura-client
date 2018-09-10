package com.boclips.kalturaclient;

import java.time.Instant;

public class RestSessionGenerator implements SessionGenerator {
    private SessionRetriever sessionRetriever;
    private Integer sessionTtl;
    private Instant sessionExpiresAt = null;
    private String currentSession = null;

    public RestSessionGenerator(SessionRetriever sessionRetriever, Integer secondsTtl) {
        this.sessionRetriever = sessionRetriever;
        this.sessionTtl = secondsTtl;
    }

    public KalturaSession get() {
        if (currentSession == null || hasExpired()) {
            generateSession();
        }

        return new KalturaSession(this.currentSession, this.sessionExpiresAt);
    }

    private boolean hasExpired() {
        return Instant.now().toEpochMilli() > sessionExpiresAt.toEpochMilli();
    }

    private void generateSession() {
        this.currentSession = this.sessionRetriever.fetch();
        this.sessionExpiresAt = Instant.ofEpochMilli(Instant.now().toEpochMilli() + sessionTtl * 1000);
    }
}
