package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.http.RequestFilters;

import java.util.List;

public interface MediaList {
    List<MediaEntry> get(RequestFilters filters);

    Long count(RequestFilters filters);
}
