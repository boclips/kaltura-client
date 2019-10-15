package com.boclips.kalturaclient.flavorParams;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class FlavorParams {
    private Integer id;

    private Integer height;
    private Integer width;

    private Quality quality;
}
