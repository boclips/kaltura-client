package com.boclips.kalturaclient;

import com.boclips.kalturaclient.captionasset.CaptionAsset;
import lombok.val;

import java.net.URI;
import java.util.Collections;
import java.util.List;

public interface KalturaCaptionManager extends KalturaEntryManager {

    CaptionAsset createCaptionForVideo(String entryId, CaptionAsset captionAsset, String content);

    List<CaptionAsset> getCaptionsForVideo(String entryId);

    String getCaptionContent(String captionAssetId);

    URI getCaptionAssetUrl(String captionAssetId);

    void deleteCaption(String captionAssetId);

    default void requestCaption(String entryId) {
        tag(entryId, Collections.singletonList(CaptionRequest.CAPTION_3PLAY.tag));
    }

    default CaptionAsset getHumanGeneratedCaptionAsset(String entryId) {
        return getCaptionsForVideo(entryId).stream()
                .filter(CaptionAsset::isHumanGenerated)
                .findFirst()
                .orElse(null);
    }

    default CaptionStatus getCaptionStatus(String entryId) {
        val captionsForVideo = getCaptionsForVideo(entryId);
        val baseEntry = getBaseEntry(entryId);
        val hasCaptions = captionsForVideo.size() > 0;

        val hasHumanGeneratedCaptions = captionsForVideo.stream()
                .anyMatch(CaptionAsset::isHumanGenerated);

        if (hasCaptions && hasHumanGeneratedCaptions) {
            return CaptionStatus.HUMAN_GENERATED_AVAILABLE;
        }
        if (baseEntry == null) {
            return CaptionStatus.UNKNOWN;
        }
        if (baseEntry.isTaggedWith(CaptionRequest.CAPTION_3PLAY.tag)) {
            return CaptionStatus.REQUESTED;
        }
        if (hasCaptions) {
            return CaptionStatus.AUTO_GENERATED_AVAILABLE;
        }
        if (baseEntry.isTaggedWith("processing")) {
            return CaptionStatus.PROCESSING;
        }
        if (baseEntry.isTagged()) {
            return CaptionStatus.UNKNOWN;
        }

        return CaptionStatus.NOT_AVAILABLE;
    }

    enum CaptionRequest {
        CAPTION_3PLAY("3play");
        private final String tag;

        CaptionRequest(String tag) {
            this.tag = tag;
        }
    }

    enum CaptionStatus {
        REQUESTED, PROCESSING, AUTO_GENERATED_AVAILABLE, HUMAN_GENERATED_AVAILABLE, NOT_AVAILABLE, UNKNOWN
    }
}
