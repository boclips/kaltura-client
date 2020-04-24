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

    void tagByEntryId(String entryId, List<String> tags);

    default void tagByReferenceId(String referenceId, List<String> tags) {
        tagByEntryId(entryIdFromReferenceId(referenceId), tags);
    }

    default void requestCaptionsByEntryId(String entryId) {
        tagByEntryId(entryId, Arrays.asList(CaptionRequest.DEFAULT_LANGUAGE_48_HOURS.tag));
    }

    default void requestCaptionsByReferenceId(String referenceId) {
        requestCaptionsByEntryId(entryIdFromReferenceId(referenceId));
    }

    default CaptionStatus getCaptionStatusByEntryId(String entryId) {
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

    default CaptionStatus getCaptionStatusByReferenceId(String referenceId) {
        return getCaptionStatusByEntryId(entryIdFromReferenceId(referenceId));
    }

    void deleteCaptionContentByAssetId(String assetId);

}
