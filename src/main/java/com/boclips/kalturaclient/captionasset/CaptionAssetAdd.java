package com.boclips.kalturaclient.captionasset;

public interface CaptionAssetAdd {

    CaptionAsset post(String sessionToken, String entryId, CaptionAsset captionAsset);
}
