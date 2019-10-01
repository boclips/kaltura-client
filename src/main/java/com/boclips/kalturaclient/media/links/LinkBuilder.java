package com.boclips.kalturaclient.media.links;

import com.boclips.kalturaclient.KalturaClientConfig;
import com.boclips.kalturaclient.media.streams.StreamFormat;
import com.damnhandy.uri.template.UriTemplate;

public class LinkBuilder {
    private final KalturaClientConfig config;

    public LinkBuilder(KalturaClientConfig config) {
        this.config = config;
    }

    /**
     * @param entryId
     * @param streamingTechnique
     * @return A url to the stream manifest file
     * @see <a href="https://developer.kaltura.com/api-docs/Deliver-and-Distribute-Media/playManifest-streaming-api.html">The playManifest Service: Streaming API for Videos and Playlists</a>
     */
    public String getStreamUrl(String entryId, StreamFormat streamingTechnique) {
        return UriTemplate.fromTemplate(
                "https://cdnapisec.kaltura.com" +
                        "/p/{partnerId}" +
                        "/sp/{partnerId}00" +
                        "/playManifest" +
                        "/entryId/{entryId}" +
                        "/format/{format}" +
                        "/flavorParamIds/{flavorParamIds}" +
                        "/protocol/https/video.mp4"
        )
                .set("partnerId", config.getPartnerId())
                .set("entryId", entryId)
                .set("format", streamingTechnique.getCode())
                .set("flavorParamIds", config.getStreamFlavorParamIds())
                .expand();
    }

    /**
     * @param entryId
     * @return A templated URL:
     * <ul>
     * <li>
     * thumbnailWidth - width in pixels of the thumbnail to be returned
     * </li>s
     * </ul>
     */
    public String getThumbnailUrl(String entryId) {
        return UriTemplate.fromTemplate(
                "https://cdnapisec.kaltura.com" +
                        "/p/{partnerId}" +
                        "/thumbnail" +
                        "/entry_id/{entryId}" +
                        "/width/{thumbnailWidth}" +
                        "/vid_slices/3" +
                        "/vid_slice/1"
        )
                .set("partnerId", config.getPartnerId())
                .set("entryId", entryId)
                .expandPartial();
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
        return UriTemplate.fromTemplate(
                "https://cdnapisec.kaltura.com" +
                        "/p/{partnerId}" +
                        "/thumbnail" +
                        "/entry_id/{entryId}" +
                        "/width/{thumbnailWidth}" +
                        "/vid_slices/{thumbnailCount}"
        )
                .set("partnerId", config.getPartnerId())
                .set("entryId", entryId)
                .expandPartial();
    }


}
