package com.boclips.kalturaclient.http;

import lombok.Builder;
import lombok.Data;
import java.io.InputStream;
import kong.unirest.ContentType;

@Builder
@Data
public class UploadFileDescriptor {
    private InputStream fileStream;
    private String filename;
    private String fieldName;
    private ContentType contentType;
}
