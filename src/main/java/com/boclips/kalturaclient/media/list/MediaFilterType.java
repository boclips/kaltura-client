package com.boclips.kalturaclient.media.list;

public enum MediaFilterType {
    CREATED_AT_GREATER_THAN_OR_EQUAL("filter[createdAtGreaterThanOrEqual]"),
    CREATED_AT_LESS_THAN_OR_EQUAL("filter[createdAtLessThanOrEqual]"),
    STATUS_IN("filter[statusIn]"),
    STATUS_NOT_IN("filter[statusNotIn]"),
    PAGE_INDEX("pager[pageIndex]"),
    PAGE_SIZE("pager[pageSize]"),
    ORDER_BY("filter[orderBy]");

    private String value;

    MediaFilterType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
