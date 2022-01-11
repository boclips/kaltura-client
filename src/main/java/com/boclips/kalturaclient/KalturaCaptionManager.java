package com.boclips.kalturaclient;

import com.boclips.kalturaclient.captionasset.CaptionAsset;
import com.boclips.kalturaclient.captionsProvider.CaptionProvider;
import com.boclips.kalturaclient.captionsProvider.CaptionProviderCaptionStatus;

import java.net.URI;
import java.util.List;

public interface KalturaCaptionManager extends KalturaEntryManager {

    CaptionAsset createCaptionForVideo(String entryId, CaptionAsset captionAsset, String content);

    List<CaptionAsset> getCaptionsForVideo(String entryId);

    String getCaptionContent(String captionAssetId);

    URI getCaptionAssetUrl(String captionAssetId);

    void deleteCaption(String captionAssetId);

    void requestCaption(String entryId);

    CaptionStatus getCaptionStatus(String entryId);

    CaptionProvider getCaptionProvider();

    CaptionAsset getHumanGeneratedCaptionAsset(String entryId);
}
