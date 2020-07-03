package com.boclips.kalturaclient.http;

import lombok.Builder;
import lombok.Data;
import org.apache.http.entity.ContentType;

import java.io.InputStream;

@Builder
@Data
public class UploadFileDescriptor {

    private InputStream fileStream;
    private String filename;
    private String fieldName;
    private ContentType contentType;


}
