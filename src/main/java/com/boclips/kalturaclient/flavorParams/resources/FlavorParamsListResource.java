package com.boclips.kalturaclient.flavorParams.resources;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @see <a href="https://developer.kaltura.com/api-docs/General_Objects/Objects/KalturaFlavorParamsListResponse">KalturaFlavorParamsListResponse</a>
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlavorParamsListResource {
    public List<FlavorParamsResource> objects;
    public Integer totalCount;
}
