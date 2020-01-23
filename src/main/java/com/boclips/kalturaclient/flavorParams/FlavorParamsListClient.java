package com.boclips.kalturaclient.flavorParams;

import com.boclips.kalturaclient.flavorParams.resources.FlavorParamsListResource;
import com.boclips.kalturaclient.http.KalturaRestClient;

import java.util.*;

/**
 * @see <a href="https://developer.kaltura.com/api-docs/service/flavorParams/action/list">flavorParams.list</a>
 */
public class FlavorParamsListClient {
    private final KalturaRestClient client;
    private final FlavorParamsProcessor processor;

    public FlavorParamsListClient(KalturaRestClient client) {
        this.client = client;
        this.processor = new FlavorParamsProcessor();
    }

    public List<FlavorParams> get() {
        FlavorParamsListResource resourceList = retrieveResourceList();

        return this.processor.process(resourceList);
    }

    private FlavorParamsListResource retrieveResourceList() {
        Map<String, Object> params = new HashMap<>();
        params.put("pager[pageSize]", 300);
        params.put("pager[pageIndex]", 0);
        params.put("pager[objectType]", "KalturaFilterPager");

        Optional<FlavorParamsListResource> optionalFlavours
                = Optional.ofNullable(client.get("/flavorparams/action/list", params, FlavorParamsListResource.class));

        return optionalFlavours
                .orElse(FlavorParamsListResource.builder().objects(Collections.emptyList()).totalCount(0).build());
    }

}
