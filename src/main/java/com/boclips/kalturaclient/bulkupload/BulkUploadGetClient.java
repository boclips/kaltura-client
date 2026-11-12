package com.boclips.kalturaclient.bulkupload;

import com.boclips.kalturaclient.bulkupload.resources.BulkUploadResource;
import com.boclips.kalturaclient.http.KalturaRestClient;

import static java.util.Collections.singletonMap;

public class BulkUploadGetClient implements BulkUploadGet {
    private final KalturaRestClient client;
    private final BulkUploadProcessor processor = new BulkUploadProcessor();

    public BulkUploadGetClient(KalturaRestClient client) {
        this.client = client;
    }

    @Override
    public BulkUpload get(String bulkUploadId) {
        BulkUploadResource resource = client.get("/bulkupload_bulk/action/get", singletonMap("id", bulkUploadId), BulkUploadResource.class);
        return processor.process("bulk upload get", resource);
    }
}
