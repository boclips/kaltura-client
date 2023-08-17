package com.boclips.kalturaclient.config;

import com.boclips.kalturaclient.captionsProvider.CaptionProviderConfig;

public class KalturaClientConfig {
    private final String baseUrl;
    private final Integer sessionTtl;
    private final Integer streamingLinkSessionTtlHours;
    private final Integer partnerId;
    private final String userId;
    private final String secret;
    private final String captionsProviderApiKey;
    private final String captionsProviderHostname;

    private KalturaClientConfig(Integer partnerId, String userId, String secret, String baseUrl, Integer sessionTtl, String captionsProviderApiKey, String captionsProviderHostname, Integer streamingLinkSessionTtlHours) {
        this.partnerId = partnerId;
        this.userId = userId;
        this.secret = secret;
        this.baseUrl = baseUrl;
        this.sessionTtl = sessionTtl;
        this.streamingLinkSessionTtlHours = streamingLinkSessionTtlHours;
        this.captionsProviderApiKey = captionsProviderApiKey;
        this.captionsProviderHostname = captionsProviderHostname;
    }

    public CaptionProviderConfig createCaptionProviderConfig() {
        return new CaptionProviderConfig(this.captionsProviderApiKey, this.captionsProviderHostname);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Integer getSessionTtl() {
        return sessionTtl;
    }

    public Integer getStreamingLinkSessionTtlHours() {
        return streamingLinkSessionTtlHours;
    }

    public Integer getPartnerId() {
        return partnerId;
    }

    public String getUserId() {
        return userId;
    }

    public String getSecret() {
        return secret;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String baseUrl = "https://www.kaltura.com/api_v3/service";
        private Integer sessionTtl = 60;
        private Integer streamingLinkSessionTtlHours = 48;
        private Integer partnerId;
        private String userId;
        private String secret;
        private String captionProviderApiKey = null;
        private String captionProviderHostname = null;

        private Builder() {
        }

        public KalturaClientConfig build() {
            validate();

            return new KalturaClientConfig(partnerId, userId, secret, baseUrl, sessionTtl, captionProviderApiKey, captionProviderHostname, streamingLinkSessionTtlHours);
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder sessionTtl(Integer sessionTtl) {
            this.sessionTtl = sessionTtl;
            return this;
        }

        public Builder streamingLinkSessionTtlHours(Integer streamingLinkSessionTtlHours) {
            this.streamingLinkSessionTtlHours = streamingLinkSessionTtlHours;
            return this;
        }

        public Builder partnerId(Integer partnerId) {
            this.partnerId = partnerId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder secret(String secret) {
            this.secret = secret;
            return this;
        }

        public Builder captionProviderApiKey(String captionsProviderApiKey) {
            this.captionProviderApiKey = captionsProviderApiKey;
            return this;
        }

        public Builder captionProviderHostname(String captionsProviderHostname) {
            this.captionProviderHostname = captionsProviderHostname;
            return this;
        }

        private void validate() {
            if (this.partnerId == null) {
                throw new KalturaClientConfigException("partner id not specified");
            }
            if (isNullOrEmpty(this.userId)) {
                throw new KalturaClientConfigException(String.format("Invalid user id: [%s]", this.userId));
            }
            if (isNullOrEmpty(this.secret)) {
                throw new KalturaClientConfigException(String.format("Invalid secret: [%s]", this.secret));
            }
            if (isNullOrEmpty(this.captionProviderApiKey)) {
                throw new KalturaClientConfigException(String.format("Invalid captionProviderApiKey: [%s]", this.captionProviderApiKey));
            }
            if (isNullOrEmpty(this.captionProviderHostname)) {
                throw new KalturaClientConfigException(String.format("Invalid captionProviderHostname: [%s]", this.captionProviderHostname));
            }
        }

        private boolean isNullOrEmpty(String input) {
            return (input == null || input.isEmpty());
        }
    }
}
