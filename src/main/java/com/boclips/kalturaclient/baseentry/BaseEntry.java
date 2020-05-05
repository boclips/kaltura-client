package com.boclips.kalturaclient.baseentry;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder(toBuilder = true)
public class BaseEntry {
    private final String id;
    private final List<String> tags;
    private final String thumbnailUrl;

    public boolean isTaggedWith(String tag) {
        return this.getTags() != null
                && this.getTags().contains(tag);
    }

    public boolean isTagged() {
        return this.getTags() != null
                && this.getTags().size() > 0;
    }
}
