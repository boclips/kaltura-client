package com.boclips.kalturaclient.captionasset;

public interface CaptionAssetAdd {

    void post(String sessionToken, String entryId, CaptionAsset captionAsset);
}
