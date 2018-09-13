package com.boclips.kalturaclient.client.http;

public enum ResponseObjectType {
    KALTURA_MEDIA_LIST_RESPONSE("KalturaMediaListResponse");

    private String type;

    public static boolean isSuccessful(String objectType) {
        return KALTURA_MEDIA_LIST_RESPONSE.getType().equals(objectType);
    }

    ResponseObjectType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
