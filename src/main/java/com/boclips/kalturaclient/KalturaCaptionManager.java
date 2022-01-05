package com.boclips.kalturaclient;

import com.boclips.kalturaclient.captionasset.CaptionAsset;
import com.boclips.kalturaclient.captionsProvider.CaptionProviderCaptionStatus;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static com.boclips.kalturaclient.captionsProvider.CaptionProviderCaptionStatus.CANCELLED;


public interface KalturaCaptionManager extends KalturaEntryManager {
    @Slf4j
    final class LogHolder {
    }

    CaptionAsset createCaptionForVideo(String entryId, CaptionAsset captionAsset, String content);

    List<CaptionAsset> getCaptionsForVideo(String entryId);

    String getCaptionContent(String captionAssetId);

    URI getCaptionAssetUrl(String captionAssetId);

    void deleteCaption(String captionAssetId);

    CaptionProviderCaptionStatus getCaptionStatusFromCaptionProvider(String title, String entryId);

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
        LogHolder.log.info("Fetching caption status for: " + entryId);
        val captionsForVideo = getCaptionsForVideo(entryId);
        val baseEntry = getBaseEntry(entryId);
        val hasCaptions = captionsForVideo.size() > 0;

        val hasHumanGeneratedCaptions = captionsForVideo.stream()
                .anyMatch(CaptionAsset::isHumanGenerated);

        if (hasCaptions && hasHumanGeneratedCaptions) {
            return CaptionStatus.HUMAN_GENERATED_AVAILABLE;
        }
        if (baseEntry == null) {
            LogHolder.log.info("Cannot find base entry for: " + entryId + ", falling back to unknown caption status");
            return CaptionStatus.UNKNOWN;
        }
        if (baseEntry.isTaggedWith(CaptionRequest.CAPTION_3PLAY.tag)) {
            return CaptionStatus.REQUESTED;
        }
        if (baseEntry.hasCategory("3play_processed") && CANCELLED.equals(getCaptionStatusFromCaptionProvider(baseEntry.getName(), baseEntry.getId()))) {
            return CaptionStatus.NOT_AVAILABLE;
        }
        if (baseEntry.hasCategory("3play_processed")) {
            return CaptionStatus.PROCESSING;
        }
        if (hasCaptions) {
            return CaptionStatus.AUTO_GENERATED_AVAILABLE;
        }
        if (baseEntry.isTagged()) {
            LogHolder.log.info("Base entry is already tagged for: " + entryId + " with" + baseEntry.getTags() + " tags, falling back to unknown caption status");
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
