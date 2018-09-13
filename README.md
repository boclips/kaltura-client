# Klient
[![](https://jitpack.io/v/knowledgemotion/kaltura-client.svg)](https://jitpack.io/#knowledgemotion/kaltura-client)

After using the *official* Java Kaltura Client for v3, we started writing our own wrapper.

## Use
```
compile 'com.github.knowledgemotion:kaltura-client:<tag>'
```

Check different [versions](https://jitpack.io/#knowledgemotion/kaltura-client/master).

## Test

The tests consist of offline unit and integration tests:

    ./gradlew test

In order to run the contract tests (which require internet connectivity), run:

    ./gradlew testContract

In order for them to run successfully, the following environment variables need to be present:

    PARTNER_ID
    USER_ID
    SECRET

Execute `./setup-contract-test` to download some secret configuration from lastpass.