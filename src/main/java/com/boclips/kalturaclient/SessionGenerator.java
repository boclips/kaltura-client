package com.boclips.kalturaclient;

public interface SessionGenerator {
    KalturaSession generate(int ttl);
}
