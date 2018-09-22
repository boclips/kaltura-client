package com.boclips.kalturaclient.media;

import com.boclips.kalturaclient.http.RequestFilters;
import com.boclips.kalturaclient.media.resources.MediaEntryResource;
import com.boclips.kalturaclient.session.SessionGenerator;

import java.util.List;

public interface MediaList {
    List<MediaEntryResource> get(SessionGenerator sessionToken, RequestFilters filters);

    Long count(String sessionToken, RequestFilters filters);
}
