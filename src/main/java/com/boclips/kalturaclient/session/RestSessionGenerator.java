package com.boclips.kalturaclient.session;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
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
        return Instant.now().toEpochMilli() + 5000 > sessionExpiresAt.toEpochMilli();
    }

    private void generateSession() {
        log.debug("Generating a session with {} TTL", sessionTtl);
        this.currentSession = this.sessionRetriever.fetch();
        this.sessionExpiresAt = Instant.ofEpochMilli(Instant.now().toEpochMilli() + sessionTtl * 1000);
    }
}
