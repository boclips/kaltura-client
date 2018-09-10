package com.boclips.kalturaclient;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
class KalturaClientConfig {
    @NonNull
    private final String baseUrl;

    @NonNull
    private final String partnerId;

    @NonNull
    private final String userId;

    @NonNull
    private final String secret;

    @NonNull
    @Builder.Default
    private final Integer sessionTtl = 60;
}
