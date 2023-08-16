package com.boclips.kalturaclient.media.links;

import com.boclips.kalturaclient.config.KalturaClientConfig;
import com.kaltura.client.ClientBase;
import com.kaltura.client.Configuration;
import com.kaltura.client.enums.SessionType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StreamUrlSessionGenerator {
    private KalturaClientConfig config;
    private final ClientBase client;

    public StreamUrlSessionGenerator(KalturaClientConfig config) {
        this.client = new ClientBase(new Configuration());
        this.config = config;
    }

    public String getForEntry(String entryId) throws Exception {
        return client.generateSessionV2(
                this.config.getSecret(),
                this.config.getUserId(),
                SessionType.USER,
                Integer.parseInt(this.config.getPartnerId()),
                getTtlInSeconds(this.config.getStreamingLinkSessionTtlHours()),
                "sview:" + entryId
        );
    }

    private int getTtlInSeconds(Integer ttlHours) {
        return ttlHours * 60 * 60;
    }
}
