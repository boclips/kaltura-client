# Klient  [![CircleCI](https://dl.circleci.com/status-badge/img/gh/boclips/kaltura-client/tree/main.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/boclips/kaltura-client/tree/main)

After trying the *official* Java Kaltura Client, we felt strong urge to write our own.
The goal of *Klient* is to make interactions with Kaltura less painful and fix various shortcomings.

## Use
Latest version: [![](https://jitpack.io/v/boclips/kaltura-client.svg)](https://jitpack.io/#boclips/kaltura-client)

```
implementation("com.github.boclips:kaltura-client:<tag>")
```

Check different available [versions](https://jitpack.io/#boclips/kaltura-client).

## Test

The tests consist of offline unit and integration tests:

    ./gradlew test

In order to run the contract tests (which require internet connectivity), run:

    ./gradlew testContract

In order for them to run successfully, the following environment variables need to be present:

    PARTNER_ID
    USER_ID
    SECRET

Execute `src/test_contract/resources/setup-contract-test` to fetch them from the Concourse credential store with `bo`.
