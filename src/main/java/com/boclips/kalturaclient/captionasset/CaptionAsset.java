package com.boclips.kalturaclient.captionasset;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder=true)
public class CaptionAsset {

    private final String id;
    private final String label;
    private final KalturaLanguage language;
    private final CaptionFormat fileType;
    private final boolean defaultCaption;

}


