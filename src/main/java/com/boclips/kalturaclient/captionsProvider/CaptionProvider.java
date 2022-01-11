package com.boclips.kalturaclient.captionsProvider;

public interface CaptionProvider {

    CaptionProviderCaptionStatus getCaptionStatus(String assetName, String entryId);
    String uploadedToProviderTag();
    String getUploadRequestTag();
}
