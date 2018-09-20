package com.boclips.kalturaclient.client.media;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestFilters {
    private List<RequestFilter> filters = new ArrayList();

    public RequestFilters() {
    }

    public RequestFilters add(String key, String value) {
        filters.add(new RequestFilter(key, value));
        return this;
    }

    public Map<String, Object> toMap() {
        return filters.stream().collect(Collectors.toMap(RequestFilter::getKey, RequestFilter::getValue));
    }

    @Getter
    private class RequestFilter {
        private String key;
        private String value;

        RequestFilter(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
