package com.boclips.kalturaclient.media.resources;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class MediaListResource {
    public List<MediaEntryResource> objects;
    public String objectType;
    public String code;
    public Long totalCount;
}
