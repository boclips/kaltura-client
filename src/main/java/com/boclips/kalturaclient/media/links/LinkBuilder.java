package com.boclips.kalturaclient.media.links;

import com.boclips.kalturaclient.KalturaClient;
import com.boclips.kalturaclient.media.streams.StreamFormat;
import com.damnhandy.uri.template.UriTemplate;

import java.util.stream.Collectors;

public class LinkBuilder {
    private final KalturaClient kalturaClient;
    private final StreamUrlSessionGenerator streamUrlSessionGenerator;

    private final String BASE_THUMBNAIL_URL =
        "https://cdnapisec.kaltura.com/p/{partnerId}/thumbnail/entry_id/{entryId}/width/{thumbnailWidth}";


    public LinkBuilder(KalturaClient kalturaClient, StreamUrlSessionGenerator streamUrlSessionGenerator) {
        this.kalturaClient = kalturaClient;
        this.streamUrlSessionGenerator = streamUrlSessionGenerator;
    }

    /**
     * @param entryId
     * @param streamingTechnique
     * @return A url to the stream manifest file
     * @see <a href="https://developer.kaltura.com/api-docs/Deliver-and-Distribute-Media/playManifest-streaming-api.html">The playManifest Service: Streaming API for Videos and Playlists</a>
     */
    public String getStreamUrl(String entryId, StreamFormat streamingTechnique, boolean includeSession) {
        UriTemplate streamLinkTemplate = UriTemplate.fromTemplate(
                "https://cdnapisec.kaltura.com" +
                        "/p/{partnerId}" +
                        "/sp/{partnerId}00" +
                        "/playManifest" +
                        "/entryId/{entryId}" +
                        "/format/{format}" +
                        (includeSession ? "/ks/{kalturaSession}" : "") +
                        "/flavorParamIds/{flavorParamIds}" +
                        "/protocol/https/video.mp4"
        )
                .set("partnerId", kalturaClient.getConfig().getPartnerId())
                .set("entryId", entryId)
                .set("format", streamingTechnique.getCode())
                .set("flavorParamIds", kalturaClient.getFlavorParams()
                        .stream()
                        .map(flavorParams -> String.valueOf(flavorParams.getId()))
                        .collect(Collectors.joining(","))
                );

        if (includeSession) {
            try {
                streamLinkTemplate.set("kalturaSession", this.streamUrlSessionGenerator.getForEntry(entryId));
            } catch (Exception e) {
                throw new GenerateKalturaSessionException(entryId, e);
            }
        }

        return streamLinkTemplate.expand();
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
        return UriTemplate.fromTemplate(BASE_THUMBNAIL_URL + "/vid_slices/3/vid_slice/1")
            .set("partnerId", kalturaClient.getConfig().getPartnerId())
            .set("entryId", entryId)
            .expandPartial();
    }

    /**
     * @param entryId
     * @param second
     * @return A templated URL to a thumbnail generated from a second:
     * <ul>
     * <li>
     * thumbnailWidth - width in pixels of the thumbnail to be returned
     * </li>
     * </ul>
     * @see <a href="https://developer.kaltura.com/api-docs/Engage_and_Publish/kaltura-thumbnail-api.html">Kaltura Video Thumbnail and Image Transformation API</a>
     */
    public String getThumbnailUrlBySecond(String entryId, Integer second) {
        return UriTemplate.fromTemplate(BASE_THUMBNAIL_URL + "/vid_sec/{second}")
            .set("partnerId", kalturaClient.getConfig().getPartnerId())
            .set("entryId", entryId)
            .set("second", second)
            .expandPartial();
    }

    /**
     * @param entryId
     * @return A default URL to a uploaded thumbnail
     * <ul>
     * <li>
     * thumbnailWidth - width in pixels of the thumbnail to be returned
     * </li>
     * </ul>
     * @see <a href="https://developer.kaltura.com/api-docs/Engage_and_Publish/kaltura-thumbnail-api.html">Kaltura Video Thumbnail and Image Transformation API</a>
     */
    public String getDefaultThumbnailUrl(String entryId) {
        return UriTemplate.fromTemplate(BASE_THUMBNAIL_URL)
                .set("partnerId", kalturaClient.getConfig().getPartnerId())
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
                .set("partnerId", kalturaClient.getConfig().getPartnerId())
                .set("entryId", entryId)
                .expandPartial();
    }


}
