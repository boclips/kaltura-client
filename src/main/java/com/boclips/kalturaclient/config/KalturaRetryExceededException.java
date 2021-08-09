package com.boclips.kalturaclient.config;

public class KalturaRetryExceededException extends RuntimeException {
    public KalturaRetryExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
