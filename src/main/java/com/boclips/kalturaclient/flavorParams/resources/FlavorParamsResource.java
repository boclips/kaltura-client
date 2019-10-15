package com.boclips.kalturaclient.flavorParams.resources;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @see <a href="https://developer.kaltura.com/api-docs/General_Objects/Objects/KalturaFlavorParams">KalturaFlavorParams</a>
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlavorParamsResource {
    private Integer id;
    private Integer height;
    private Integer width;
    private Integer videoBitrate;
    private String objectType;
}
