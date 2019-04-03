package com.boclips.kalturaclient.baseentry;

import com.boclips.kalturaclient.baseentry.resources.BaseEntryResource;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder(toBuilder = true)
public class BaseEntry {
    private final String id;
    private final List<String> tags;
}
