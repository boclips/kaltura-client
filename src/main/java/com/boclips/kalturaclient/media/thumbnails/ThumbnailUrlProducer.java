package com.boclips.kalturaclient.media.thumbnails;

import com.boclips.kalturaclient.KalturaClientConfig;
import com.boclips.kalturaclient.media.resources.MediaEntryResource;

public class ThumbnailUrlProducer {
    private static final String THUMBNAIL_URL_TEMPLATE = "https://cdnapisec.kaltura.com/p/%s/thumbnail/entry_id/%s/width/{thumbnailWidth}/vid_slices/3/vid_slice/1";
    private final KalturaClientConfig config;

    public ThumbnailUrlProducer(KalturaClientConfig config) {
        this.config = config;
    }

    public String convert(MediaEntryResource mediaEntryResource) {
        return convert(mediaEntryResource.getId());
    }

    public String convert(String entryId) {
        return String.format(THUMBNAIL_URL_TEMPLATE, config.getPartnerId(), entryId);
    }
}
