package com.boclips.kalturaclient.media.streams;

import com.boclips.kalturaclient.KalturaClientConfig;
import com.boclips.kalturaclient.media.resources.MediaEntryResource;

public class StreamUrlProducer {
    private static final String STREAMING_URL_TEMPLATE = "https://cdnapisec.kaltura.com/p/%s/sp/%s00/playManifest/entryId/%s/format/[FORMAT]/protocol/https/video.mp4";
    private final KalturaClientConfig config;

    public StreamUrlProducer(KalturaClientConfig config) {
        this.config = config;
    }

    public StreamUrls convert(MediaEntryResource mediaEntryResource) {
        return new StreamUrls(String.format(STREAMING_URL_TEMPLATE,
                config.getPartnerId(), config.getPartnerId(), mediaEntryResource.getId()));
    }
}
