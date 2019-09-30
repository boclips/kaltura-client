package com.boclips.kalturaclient.media.links;

import com.boclips.kalturaclient.KalturaClientConfig;
import com.boclips.kalturaclient.media.streams.StreamFormat;

public class LinkBuilder {
    private final KalturaClientConfig config;

    public LinkBuilder(KalturaClientConfig config) {
        this.config = config;
    }

    public String getStreamUrl(String entryId, StreamFormat streamingTechnique) {
        String template = "https://cdnapisec.kaltura.com/p/%s/sp/%s00/playManifest/entryId/%s/format/%s/protocol/https/video.mp4";

        return String.format(template, config.getPartnerId(), config.getPartnerId(), entryId, streamingTechnique.getCode());
    }

    /**
     * @param entryId
     * @return A templated URL:
     * <ul>
     * <li>
     * thumbnailWidth - width in pixels of the thumbnail to be returned
     * </li>
     * </ul>
     */
    public String getThumbnailUrl(String entryId) {
        String template = "https://cdnapisec.kaltura.com/p/%s/thumbnail/entry_id/%s/width/{thumbnailWidth}/vid_slices/3/vid_slice/1";

        return String.format(template, config.getPartnerId(), entryId);
    }

    /**
     * @param entryId
     * @return A templated URL:
     * <ul>
     * <li>
     * thumbnailWidth - width in pixels of the thumbnail to be returned
     * </li>
     * <li>
     * thumbnailCount - number of thumbnails to be returned in image
     * </li>
     * </ul>
     */
    public String getVideoPreviewUrl(String entryId) {
        String template = "https://cdnapisec.kaltura.com/p/%s/thumbnail/entry_id/%s/width/{thumbnailWidth}/vid_slices/{thumbnailCount}";

        return String.format(template, config.getPartnerId(), entryId);
    }


}
