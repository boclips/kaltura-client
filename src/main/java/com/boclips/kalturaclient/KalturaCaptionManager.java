package com.boclips.kalturaclient;

import com.boclips.kalturaclient.captionasset.CaptionAsset;
import lombok.val;

import java.util.Collections;
import java.util.List;

public interface KalturaCaptionManager extends KalturaEntryManager {

    CaptionAsset createCaptionForVideo(String entryId, CaptionAsset captionAsset, String content);

    List<CaptionAsset> getCaptionsForVideo(String entryId);

    String getCaptionContent(String captionAssetId);

    void deleteCaption(String captionAssetId);

    default void requestCaption(String entryId) {
        tag(entryId, Collections.singletonList(CaptionRequest.DEFAULT_LANGUAGE_48_HOURS.tag));
    }

    default CaptionStatus getCaptionStatus(String entryId) {
        val captionsForVideo = getCaptionsForVideo(entryId);

        if (captionsForVideo.size() > 0) {
            val hasHumanGeneratedCaptions = captionsForVideo.stream()
                    .anyMatch(captionAsset -> !captionAsset.getLabel().contains("(auto-generated)"));

            if (hasHumanGeneratedCaptions) {
                return CaptionStatus.HUMAN_GENERATED_AVAILABLE;
            } else {
                return CaptionStatus.AUTO_GENERATED_AVAILABLE;
            }
        } else {
            val baseEntry = getBaseEntry(entryId);
            if (baseEntry == null) {
                return CaptionStatus.UNKNOWN;
            }
            if (baseEntry.isTaggedWith(CaptionRequest.DEFAULT_LANGUAGE_48_HOURS.tag)) {
                return CaptionStatus.REQUESTED;
            }
            if (baseEntry.isTaggedWith("processing")) {
                return CaptionStatus.PROCESSING;
            }
            if (baseEntry.isTagged()) {
                return CaptionStatus.UNKNOWN;
            }
            return CaptionStatus.NOT_AVAILABLE;
        }
    }

    enum CaptionRequest {
        DEFAULT_LANGUAGE_48_HOURS("caption48");
        private final String tag;

        CaptionRequest(String tag) {
            this.tag = tag;
        }
    }

    enum CaptionStatus {
        REQUESTED, PROCESSING, AUTO_GENERATED_AVAILABLE, HUMAN_GENERATED_AVAILABLE, NOT_AVAILABLE, UNKNOWN
    }
}
