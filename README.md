# Klient  [![](https://ci.boclips.com/api/v1/teams/main/pipelines/kaltura-client/jobs/internal-tests/badge)](https://ci.boclips.com/teams/main/pipelines/kaltura-client) 

After trying the *official* Java Kaltura Client, we felt strong urge to write our own.
The goal of *Klient* is to make interactions with Kaltura less painful and fix various shortcomings.

## Use
Latest version: [![](https://jitpack.io/v/knowledgemotion/kaltura-client.svg)](https://jitpack.io/#knowledgemotion/kaltura-client)

```
compile 'com.github.knowledgemotion:kaltura-client:<tag>'
```

Check different available [versions](https://jitpack.io/#knowledgemotion/kaltura-client).

## Test

The tests consist of offline unit and integration tests:

    ./gradlew test

In order to run the contract tests (which require internet connectivity), run:

    ./gradlew testContract

In order for them to run successfully, the following environment variables need to be present:

    PARTNER_ID
    USER_ID
    SECRET
    FLAVOR_PARAM_IDS

Execute `src/test_contract/resources/setup-contract-test` to download some secret configuration from lastpass.
