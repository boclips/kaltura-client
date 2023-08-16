package com.boclips.kalturaclient.media.links;

public class GenerateKalturaSessionException extends RuntimeException {
    public GenerateKalturaSessionException(String source, String entryId, Throwable cause) {
        super(String.format("%s: Could not generate Kaltura Session for entryId %s", source, entryId), cause);
    }
}
