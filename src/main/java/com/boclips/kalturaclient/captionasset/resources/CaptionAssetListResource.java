package com.boclips.kalturaclient.captionasset.resources;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CaptionAssetListResource {
    public Long totalCount;
    public List<CaptionAssetResource> objects;
}



