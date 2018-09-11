package com.boclips.kalturaclient.streams;

public class StreamUrls {
    private final String urlTemplate;

    public StreamUrls(String urlTemplate) {
        this.urlTemplate = urlTemplate;
    }

    public String withFormat(StreamFormat format) {
        return urlTemplate.replace("[FORMAT]", format.getCode());
    }
}

