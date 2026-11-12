package com.boclips.kalturaclient.bulkupload;

import lombok.Value;

@Value
public class BulkUpload {
    String id;
    BulkUploadStatus status;
    String error;
}
