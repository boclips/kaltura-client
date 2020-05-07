package com.boclips.kalturaclient;

import com.boclips.kalturaclient.clients.KalturaClientV3;
import com.boclips.kalturaclient.config.KalturaClientConfig;
import com.boclips.kalturaclient.flavorParams.FlavorParams;
import com.boclips.kalturaclient.media.links.LinkBuilder;
import com.boclips.kalturaclient.session.RestSessionGenerator;
import com.boclips.kalturaclient.session.SessionRetriever;

import java.util.List;

public interface KalturaClient extends KalturaEntryManager, KalturaVideoAssetManager, KalturaCaptionManager {

    static KalturaClient create(KalturaClientConfig config) {
        return KalturaClientV3.create(config, new RestSessionGenerator(new SessionRetriever(config), config.getSessionTtl()));
    }

    List<FlavorParams> getFlavorParams();
    KalturaClientConfig getConfig();
    LinkBuilder getLinkBuilder();
}
