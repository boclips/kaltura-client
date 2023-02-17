package com.boclips.kalturaclient.thumbnailAsset;

import com.boclips.kalturaclient.http.KalturaRestClient;
import com.boclips.kalturaclient.http.UploadFileDescriptor;
import com.boclips.kalturaclient.thumbnailAsset.resources.ThumbnailAssetAddResource;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import kong.unirest.ContentType;

public class ThumbnailAssetAddClient implements ThumbnailAssetAdd {
    private final KalturaRestClient client;

    public ThumbnailAssetAddClient(KalturaRestClient restClient) {
        this.client = restClient;
    }

    @Override
    public String addThumbnailFromImage(String entryId, InputStream fileStream, String filename) {
        Map<String, Object> params = new HashMap<>();
        params.put("entryId", entryId);
        params.put("format", 1);

        UploadFileDescriptor uploadFileDescriptor = UploadFileDescriptor.builder()
                .fieldName("fileData")
                .fileStream(fileStream)
                .filename(filename)
                .contentType(ContentType.APPLICATION_OCTET_STREAM)
                .build();

        ThumbnailAssetAddResource response = this.client.postFileBody("/thumbasset/action/addFromImage", params,
                uploadFileDescriptor, ThumbnailAssetAddResource.class);
        return response.getId();
    }
}
