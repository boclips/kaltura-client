package com.boclips.kalturaclient.streams;

import com.boclips.kalturaclient.KalturaClientConfig;
import com.boclips.kalturaclient.MediaEntryResource;

public class StreamUrlProducer {

    private final KalturaClientConfig config;

    public StreamUrlProducer(KalturaClientConfig config) {
        this.config = config;
    }

    public StreamUrls convert(MediaEntryResource mediaEntryResource) {
        return new StreamUrls(String.format("https://cdnapisec.kaltura.com/p/%s/sp/%s00/playManifest/entryId/%s/format/[FORMAT]/protocol/https/video.mp4",
                config.getPartnerId(), config.getPartnerId(), mediaEntryResource.getId()));
    }
}
