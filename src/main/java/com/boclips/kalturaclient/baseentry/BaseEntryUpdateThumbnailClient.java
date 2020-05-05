package com.boclips.kalturaclient.baseentry;

import com.boclips.kalturaclient.http.KalturaRestClient;
import com.boclips.kalturaclient.media.links.LinkBuilder;

import java.util.HashMap;
import java.util.Map;

public class BaseEntryUpdateThumbnailClient implements BaseEntryUpdateThumbnail {
  private final KalturaRestClient client;
  private final LinkBuilder linkBuilder;

  private final Integer DEFAULT_THUMBNAIL_WIDTH = 1920;

  public BaseEntryUpdateThumbnailClient(KalturaRestClient client, LinkBuilder linkBuilder) {
    this.client = client;
    this.linkBuilder = linkBuilder;
  }

  @Override
  public void updateWithMiddleFrame(String entryId) {
    String slicedThumbnailUrl = linkBuilder.getSlicedThumbnailUrl(entryId, DEFAULT_THUMBNAIL_WIDTH);
    Map<String, Object> params = new HashMap<>();
    params.put("entryId", entryId);
    params.put("url", slicedThumbnailUrl);
    client.post("/baseentry/action/updateThumbnailFromUrl", params, String.class);
  }
}
