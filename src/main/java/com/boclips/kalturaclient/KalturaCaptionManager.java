package com.boclips.kalturaclient;

import com.boclips.kalturaclient.baseentry.BaseEntry;
import com.boclips.kalturaclient.captionasset.CaptionAsset;
import lombok.val;

import java.util.Arrays;
import java.util.List;

public interface KalturaCaptionManager extends KalturaEntryManager {

    enum CaptionRequest {
        DEFAULT_LANGUAGE_48_HOURS("caption48");

        private final String tag;

        CaptionRequest(String tag) {
            this.tag = tag;
        }
    }

    enum CaptionStatus {
        REQUESTED, PROCESSING, AVAILABLE, NOT_AVAILABLE, UNKNOWN
    }

    CaptionAsset createCaptionsFileWithEntryId(String entryId, CaptionAsset captionAsset, String content);

    CaptionAsset createCaptionsFile(String referenceId, CaptionAsset captionAsset, String content);

    List<CaptionAsset> getCaptionFilesByEntryId(String entryId);

    List<CaptionAsset> getCaptionFilesByReferenceId(String referenceId);

    String getCaptionContentByAssetId(String assetId);

    void tag(String entryId, List<String> tags);

    default void requestCaptions(String entryId) {
        tag(entryId, Arrays.asList(CaptionRequest.DEFAULT_LANGUAGE_48_HOURS.tag));
    }

    default CaptionStatus getCaptionStatus(String entryId) {
        if (getCaptionFilesByEntryId(entryId).size() > 0) {
            return CaptionStatus.AVAILABLE;
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

    void deleteCaptionContentByAssetId(String assetId);

}
