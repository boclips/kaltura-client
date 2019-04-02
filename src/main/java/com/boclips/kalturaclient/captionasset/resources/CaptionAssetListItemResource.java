package com.boclips.kalturaclient.captionasset.resources;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CaptionAssetListItemResource {
    public String id;
    public String label;
    public String language;
    public String format;
}
