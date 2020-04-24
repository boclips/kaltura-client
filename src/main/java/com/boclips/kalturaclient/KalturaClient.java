package com.boclips.kalturaclient;

import com.boclips.kalturaclient.baseentry.BaseEntry;
import com.boclips.kalturaclient.flavorAsset.Asset;
import com.boclips.kalturaclient.flavorParams.FlavorParams;
import com.boclips.kalturaclient.media.MediaEntry;
import com.boclips.kalturaclient.media.links.LinkBuilder;
import com.boclips.kalturaclient.session.RestSessionGenerator;
import com.boclips.kalturaclient.session.SessionRetriever;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface KalturaClient extends KalturaEntryManager, KalturaCaptionManager {
    static KalturaClient create(KalturaClientConfig config) {
        return KalturaClientV3.create(config, new RestSessionGenerator(new SessionRetriever(config), config.getSessionTtl()));
    }

    List<FlavorParams> getFlavorParams();

    KalturaClientConfig getConfig();

    LinkBuilder getLinkBuilder();
}
