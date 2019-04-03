package com.boclips.kalturaclient.baseentry.resources;

import com.boclips.kalturaclient.baseentry.BaseEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class BaseEntryResource {
    public String id;
    public String tags;

    public BaseEntry toBaseEntry() {
        return BaseEntry.builder()
                .id(id)
                .tags(Arrays.asList(tags.split(", ")))
                .build();
    }
}
