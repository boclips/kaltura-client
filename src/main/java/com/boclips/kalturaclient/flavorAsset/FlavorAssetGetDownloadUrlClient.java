package com.boclips.kalturaclient.flavorAsset;

import com.boclips.kalturaclient.http.KalturaRestClient;
import com.boclips.kalturaclient.media.links.GenerateKalturaSessionException;
import com.boclips.kalturaclient.media.links.StreamUrlSessionGenerator;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

public class FlavorAssetGetDownloadUrlClient implements FlavorAssetGetDownloadUrl {

    private final KalturaRestClient client;
    private final StreamUrlSessionGenerator sessionGenerator;

    public FlavorAssetGetDownloadUrlClient(KalturaRestClient client, StreamUrlSessionGenerator sessionGenerator) {
        this.client = client;
        this.sessionGenerator = sessionGenerator;
    }

    @Override
    public URI getDownloadUrl(String assetId, Boolean includeSession) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", assetId);
        String session;
        URIBuilder uri = new URIBuilder(this.client.get("/flavorasset/action/getUrl", map, URI.class));
        if (includeSession) {
            try {
                session = sessionGenerator.getForEntry(assetId);
            } catch (Exception ex) {
                throw new GenerateKalturaSessionException("GetDownloadUrl", assetId, ex.getCause());
            }
            List<String> pathSegments = uri.getPathSegments();
            pathSegments.add(pathSegments.size() - 2, "ks");
            pathSegments.add(pathSegments.size() - 2, session);
            uri.setPathSegments(pathSegments);
        }
        try {
            return uri.build();
        } catch (Exception ex) {
            return null;
        }
    }

}
