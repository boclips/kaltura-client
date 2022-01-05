package com.boclips.kalturaclient.baseentry;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder(toBuilder = true)
public class BaseEntry {
    private final String id;
    private final List<String> tags;
    private final List<String> categories;
    private final String thumbnailUrl;
    private final String name;

    public boolean isTaggedWith(String tag) {
        return this.getTags() != null
                && this.getTags().contains(tag);
    }

    public boolean hasCategory(String category) {
        return this.hasCategories() && this.getCategories().contains(category);
    }

    public boolean isTagged() {
        return this.getTags() != null
                && this.getTags().size() > 0;
    }

    public boolean hasCategories() {
        return this.getCategories() != null
                && this.getCategories().size() > 0;
    }

    public String getName() {
        return this.name;
    }
}
