package com.boclips.kalturaclient.bulkupload;

import java.util.Collection;

public interface MediaBulkDelete {
    BulkUpload deleteEntries(Collection<String> entryIds);
}
