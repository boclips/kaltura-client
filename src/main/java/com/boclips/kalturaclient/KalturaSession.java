package com.boclips.kalturaclient;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class KalturaSession {
    private final String token;
    private Instant expires;
}
