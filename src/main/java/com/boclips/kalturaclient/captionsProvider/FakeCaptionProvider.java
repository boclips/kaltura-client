package com.boclips.kalturaclient.captionsProvider;

import java.util.HashMap;
import java.util.Map;

public class FakeCaptionProvider implements CaptionProvider {

    private Map<String, CaptionProviderCaptionStatus> titlesToStatusMap = new HashMap<>();

    @Override
    public CaptionProviderCaptionStatus getCaptionStatus(String assetName, String entryId) {
        //in real life caption provider is queried by title, but I'm simplifying it here and using entryId
        return titlesToStatusMap.getOrDefault(entryId, CaptionProviderCaptionStatus.UNKNOWN);
    }

    public void setStatusForEntryId(String entryId, CaptionProviderCaptionStatus status) {
        titlesToStatusMap.put(entryId, status);
    }

    public void clear() {
        titlesToStatusMap = new HashMap<>();
    }
}
