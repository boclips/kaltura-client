package com.boclips.kalturaclient;

import com.boclips.kalturaclient.config.KalturaClientConfig;

// Groovy 2.4 emits bytecode for static interface method calls that JDK 9+ rejects, so the Spock
// contract test reaches KalturaClient.create through this Java class.
public final class ContractTestClients {
    private ContractTestClients() {
    }

    public static KalturaClient create(KalturaClientConfig config) {
        return KalturaClient.create(config);
    }
}
