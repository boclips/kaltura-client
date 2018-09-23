package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.http.RequestFilters;
import com.boclips.kalturaclient.session.SessionGenerator;

import java.util.List;

public interface MediaList {
    List<MediaEntry> get(SessionGenerator sessionToken, RequestFilters filters);

    Long count(String sessionToken, RequestFilters filters);
}
