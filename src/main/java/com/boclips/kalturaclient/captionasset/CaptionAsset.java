package com.boclips.kalturaclient.captionasset;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder=true)
public class CaptionAsset {

    private final String id;

    private final String label;

    private final String language;

    private final CaptionFormat fileType;

}


