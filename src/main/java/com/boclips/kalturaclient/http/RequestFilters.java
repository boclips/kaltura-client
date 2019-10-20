package com.boclips.kalturaclient.http;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestFilters {
    public static RequestFilters merge(RequestFilters filter1, RequestFilters filter2) {
        List<RequestFilter> allFilters = Stream.concat(filter1.filters.stream(), filter2.filters.stream())
                .collect(Collectors.toList());

        RequestFilters requestFilters = new RequestFilters(allFilters);

        return requestFilters;
    }

    private RequestFilters(List<RequestFilter> filters) {
        this.filters = filters;
    }

    public RequestFilters() {
    }

    public RequestFilters add(String key, String value) {
        filters.add(new RequestFilter(key, value));
        return this;
    }

    public Map<String, Object> toMap() {
        return filters.stream().collect(Collectors.toMap(RequestFilter::getKey, RequestFilter::getValue));
    }

    @Override
    public String toString() {
        return "RequestFilters{filters=" + filters + '}'; }

    private List<RequestFilter> filters = new ArrayList();

    @Getter
    private class RequestFilter {
        private String key;
        private String value;

        RequestFilter(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "RequestFilter{key='" + key + ", value='" + value + '}';
        }
    }
}
