package com.boclips.kalturaclient.bulkupload.resources;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkUploadResource {
    private String objectType;
    private Long id;
    private Integer status;
    private String error;
    private String code;
    private String message;
}
