package com.boclips.kalturaclient.media.thumbnails;

import com.boclips.kalturaclient.KalturaClientConfig;
import com.boclips.kalturaclient.media.resources.MediaEntryResource;

public class VideoPreviewUrlProducer {
    private static final String VIDEO_PREVIEW_TEMPLATE_URL = "https://cdnapisec.kaltura.com/p/%s/thumbnail/entry_id/%s/width/{thumbnailWidth}/vid_slices/{thumbnailCount}";
    private final KalturaClientConfig config;

    public VideoPreviewUrlProducer(KalturaClientConfig config) {
        this.config = config;
    }

    public String convert(MediaEntryResource mediaEntryResource) {
        return convert(mediaEntryResource.getId());
    }

    public String convert(String entryId) {
        return String.format(VIDEO_PREVIEW_TEMPLATE_URL, config.getPartnerId(), entryId);
    }
}
