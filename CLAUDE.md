A Java 11 wrapper around the [Kaltura API v3](https://developer.kaltura.com/api-docs/), offering a
cleaner interface than the official Java client. Gradle (Kotlin DSL), tested with Spock.

## Prerequisites

Lombok drives the DTOs, so an IDE without the Lombok plugin and annotation processing enabled will
show the whole codebase as broken while Gradle builds it fine.

## Commands

```bash
./gradlew build              # Compile and run the offline tests
./gradlew test               # Offline unit and integration tests only
./gradlew testContract       # Contract tests — hit the live Kaltura API, see below

# Run a single test class or method
./gradlew test --tests "com.boclips.kalturaclient.media.links.LinkBuilderTest"
./gradlew test --tests "com.boclips.kalturaclient.media.links.LinkBuilderTest.getStreamUrl*"
```

`testContract` needs `PARTNER_ID`, `USER_ID` and `SECRET` in the environment and talks to the real
Kaltura API — it is not part of `build`. Run `src/test_contract/resources/setup-contract-test` to pull
those from LastPass.

## Conventions and gotchas

- Every service follows a **Client-Processor-Resource** split: `*Client` does HTTP (`MediaListClient`),
  `*Processor` parses JSON (`MediaProcessor`), `*Resource` is the response DTO (`MediaEntryResource`).
  Adding an endpoint means all three, then wiring it into `KalturaClientV3` and whichever of the three
  manager interfaces (`KalturaEntryManager`, `KalturaVideoAssetManager`, `KalturaCaptionManager`) it
  belongs to — `KalturaClient` is just their union.
- **Only `KalturaRestClient.get` is retried.** It wraps Failsafe with a 1–15s backoff and 3 retries,
  throwing `KalturaRetryExceededException` when they are exhausted — so a caller or test expecting a
  fast failure waits instead. `post` and `postFileBody` have no retry at all, so writes fail on the
  first error.
- **Session expiry recovery is per-client, not automatic.** `appendQueryParameters` injects the `ks`
  token into every request, but nothing renews it on expiry: `MediaListClient.get` is the only place
  that catches `KalturaClientApiException`, calls `refreshSession()` and retries once. Its own
  `count()` does not, nor does any other client. A new endpoint that must survive expiry has to repeat
  that pattern explicitly.
- Tests are Spock, so `*Test.groovy` under `src/test/groovy/` with `given`/`when`/`then` blocks, not
  JUnit. Shared fixtures live in `com.boclips.kalturaclient.testsupport.TestFactories`.

## Publishing

Published via JitPack as `com.github.knowledgemotion:kaltura-client` — note the legacy
`knowledgemotion` org rather than `boclips`. Tags trigger the build.
