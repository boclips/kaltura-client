package com.boclips.kalturaclient.captionasset.resources;

import com.boclips.kalturaclient.captionasset.CaptionAsset;
import com.boclips.kalturaclient.captionasset.CaptionFormat;
import com.boclips.kalturaclient.captionasset.KalturaLanguage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder(toBuilder = true)
public class CaptionAssetResource {
    public String id;
    public String label;
    public String language;
    public String format;

    public CaptionAsset toAsset() {
        validate(id);
        validate(label);
        validate(language);
        validate(format);

        return CaptionAsset.builder()
                .id(id)
                .label(label)
                .language(KalturaLanguage.fromName(language))
                .fileType(CaptionFormat.fromValue(format))
                .build();
    }

    private void validate(String field) {
        if(field == null || field.equals("")) {
            throw new IllegalStateException("Invalid caption asset: " + this.toString());
        }
    }
}
