package com.boclips.kalturaclient.baseentry.resources;

import com.boclips.kalturaclient.baseentry.BaseEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Arrays;
import java.util.Collections;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class BaseEntryResource {
    public String id;
    public String tags;
    public String categories;
    public String thumbnailUrl;
    public String name;

    public BaseEntry toBaseEntry() {
        return BaseEntry.builder()
                .id(id)
                .tags(tags == null ? Collections.emptyList() : Arrays.asList(tags.split(", ")))
                .categories(categories == null ? Collections.emptyList() : Arrays.asList(categories.split(", ")))
                .thumbnailUrl(thumbnailUrl)
                .name(name)
                .build();
    }
}
