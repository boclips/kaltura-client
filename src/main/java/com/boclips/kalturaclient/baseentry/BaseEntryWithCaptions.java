package com.boclips.kalturaclient.baseentry;

import com.boclips.kalturaclient.CaptionStatus;
import com.boclips.kalturaclient.captionasset.CaptionAsset;
import com.boclips.kalturaclient.captionsProvider.CaptionProvider;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Slf4j
@Builder
public class BaseEntryWithCaptions {

    private String id;
    private BaseEntry baseEntry;
    private List<CaptionAsset> captions;
    private CaptionProvider captionProvider;

    public CaptionStatus getCaptionStatus() {
        log.info("Fetching caption status for: " + id);

        if (hasHumanGeneratedCaptions()) {
            return CaptionStatus.HUMAN_GENERATED_AVAILABLE;
        }

        if (baseEntry == null) {
            log.info("Cannot find base entry for: " + id + ", falling back to unknown caption status");
            return CaptionStatus.UNKNOWN;
        }

        if (baseEntry.isCaptionRequested(captionProvider.getUploadRequestTag())) {
            return CaptionStatus.REQUESTED;
        }

        if (baseEntry.isCaptionRequestCancelled(captionProvider)) {
            return CaptionStatus.NOT_AVAILABLE;
        }

        if (baseEntry.isCaptionRequestProcessing(captionProvider.uploadedToProviderTag())) {
            return CaptionStatus.PROCESSING;
        }

        if (isNotEmpty(captions)) {
            return CaptionStatus.AUTO_GENERATED_AVAILABLE;
        }

        if (baseEntry.isTagged()) {
            log.info("Base entry is already tagged for: " + id + " with" + baseEntry.getTags() + " tags, falling back to unknown caption status");
            return CaptionStatus.UNKNOWN;
        }

        return CaptionStatus.NOT_AVAILABLE;
    }

    private boolean hasHumanGeneratedCaptions(){
        return isNotEmpty(captions) && captions.stream().anyMatch(CaptionAsset::isHumanGenerated);
    }
}
