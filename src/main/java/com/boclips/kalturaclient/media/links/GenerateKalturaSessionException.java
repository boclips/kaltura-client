package com.boclips.kalturaclient.media.links;

public class GenerateKalturaSessionException extends RuntimeException {
    public GenerateKalturaSessionException(String entryId, Throwable cause) {
        super(String.format("Could not generate Kaltura Session for entryId %s", entryId), cause);
    }
}
