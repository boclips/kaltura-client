package com.boclips.kalturaclient.captionsProvider;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

@Slf4j
public class ThreePlayTranscriptURL {
    private final String apiKey;
    private final String hostname;

    public ThreePlayTranscriptURL(CaptionProviderConfig config) {
        this.apiKey = config.getApiKey();
        this.hostname = config.getHostname();;
    }

    public Optional<URL> getUrlFor(String assetId) {
        try {
            return Optional.of(new URIBuilder()
                .setScheme("https")
                .setHost(hostname)
                .setPath("/v3/transcripts")
                .setParameter("api_key", apiKey)
                .setParameter("media_file_name", assetId)
                .build()
                .toURL());
        } catch (URISyntaxException | MalformedURLException e) {
            log.warn("Unable to build URL for hostname: '{}', asset: '{}'", hostname, assetId);
            return Optional.empty();
        }
    }
}
