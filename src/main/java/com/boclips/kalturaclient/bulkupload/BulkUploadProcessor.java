package com.boclips.kalturaclient.bulkupload;

import com.boclips.kalturaclient.bulkupload.resources.BulkUploadResource;
import com.boclips.kalturaclient.http.KalturaClientApiException;

class BulkUploadProcessor {
    BulkUpload process(String action, BulkUploadResource resource) {
        if ("KalturaAPIException".equals(resource.getObjectType())) {
            throw new KalturaClientApiException(
                    String.format("Kaltura %s failed: %s %s", action, resource.getCode(), resource.getMessage()));
        }
        return new BulkUpload(
                String.valueOf(resource.getId()),
                BulkUploadStatus.fromCode(resource.getStatus()),
                resource.getError());
    }
}
