package com.boclips.kalturaclient.thumbnailAsset;

import java.io.InputStream;

public interface ThumbnailAssetAdd {

    String addThumbnailFromImage(String entryId, InputStream fileStream, String filename);
}
