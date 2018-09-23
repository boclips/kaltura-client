package test_support

import com.boclips.kalturaclient.KalturaClientConfig

class Factories {
    static KalturaClientConfig exampleConfig() {
        return KalturaClientConfig.builder()
                .baseUrl("http://base.com")
                .partnerId("partner-123")
                .secret("s3cr3t")
                .userId("user-123")
                .build()
    }
}
