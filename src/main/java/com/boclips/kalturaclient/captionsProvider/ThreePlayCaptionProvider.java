package com.boclips.kalturaclient.captionsProvider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.net.URL;

@Slf4j
@Builder
@AllArgsConstructor
public class ThreePlayCaptionProvider implements CaptionProvider {

    private final ThreePlayTranscriptURL transcriptURL;
    private final HttpTypedClient httpClient;

    public static ThreePlayCaptionProvider create(CaptionProviderConfig config) {
        return new ThreePlayCaptionProvider(new ThreePlayTranscriptURL(config), new HttpTypedClient());
    }

    @Override
    public CaptionProviderCaptionStatus getCaptionStatus(String assetName, String entryId) {
        CaptionProviderCaptionStatus status = transcriptURL.getUrlFor(assetName)
            .map(transcriptUrl -> fetchTranscriptStatus(assetName, transcriptUrl))
            .filter(this::isValid)
            .map(response -> extractStatus(entryId, response))
            .orElse(CaptionProviderCaptionStatus.IN_PROGRESS);

        if (status == CaptionProviderCaptionStatus.UNKNOWN) {
            log.warn("Could not figure out 3play status for video with entryId '{}' with title '{}'", entryId, assetName);
        }

        return status;
    }

    private CaptionStatusResponse fetchTranscriptStatus(String assetName, URL transcriptUrl) {
        try {
            return httpClient.execute(transcriptUrl, CaptionStatusResponse.class);
        } catch (IOException e) {
            log.warn(String.format("Failed to fetch 3play caption status for asset with name: '%s'", assetName), e);
            return null;
        }
    }

    private boolean isValid(CaptionStatusResponse response) {
        boolean isResponseStatusOk = response.getCode() == HttpStatus.SC_OK;
        boolean isDataEmpty = response.getData() == null || response.getData().size() == 0;

        return isResponseStatusOk && !isDataEmpty;
    }

    private CaptionProviderCaptionStatus extractStatus(String entryId, CaptionStatusResponse response) {
        return response.getData().stream()
            .filter(it -> entryId.equals(it.getReferenceId()))
            .findFirst()
            .map(CaptionStatusResponse.CaptionItem::getStatus)
            .map(CaptionProviderCaptionStatus::extract)
            .orElse(CaptionProviderCaptionStatus.UNKNOWN);
    }
}
