package com.boclips.kalturaclient.http;

import kong.unirest.ContentType;
import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

@Builder
@Data
public class UploadFileDescriptor {
    private InputStream fileStream;
    private String filename;
    private String fieldName;
    private ContentType contentType;
}
