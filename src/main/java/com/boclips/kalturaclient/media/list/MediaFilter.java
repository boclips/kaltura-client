package com.boclips.kalturaclient.media.list;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MediaFilter {
    private MediaFilterType key;
    private String value;

    public String toString() {
        return String.format("MediaFilter: %s=%s", this.key.getValue(), this.value);
    }
}

