package com.boclips.kalturaclient.captionasset.resources;

import com.boclips.kalturaclient.captionasset.CaptionAsset;
import com.boclips.kalturaclient.captionasset.CaptionFormat;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CaptionAssetResource {
    public String id;
    public String label;
    public String language;
    public String format;

    public CaptionAsset toAsset() {
        return CaptionAsset.builder()
                .id(id)
                .label(label)
                .language(language)
                .fileType(CaptionFormat.fromValue(format))
                .build();
    }
}
