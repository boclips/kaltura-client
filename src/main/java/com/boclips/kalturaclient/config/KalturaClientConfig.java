package com.boclips.kalturaclient.config;

public class KalturaClientConfig {
    private final String baseUrl;
    private final Integer sessionTtl;
    private final String partnerId;
    private final String userId;
    private final String secret;

    private KalturaClientConfig(String partnerId, String userId, String secret, String baseUrl, Integer sessionTtl) {
        this.partnerId = partnerId;
        this.userId = userId;
        this.secret = secret;
        this.baseUrl = baseUrl;
        this.sessionTtl = sessionTtl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Integer getSessionTtl() {
        return sessionTtl;
    }

    public String getPartnerId() {
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
        private String partnerId;
        private String userId;
        private String secret;

        private Builder() {
        }

        public KalturaClientConfig build() {
            validate();

            return new KalturaClientConfig(partnerId, userId, secret, baseUrl, sessionTtl);
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder sessionTtl(Integer sessionTtl) {
            this.sessionTtl = sessionTtl;
            return this;
        }

        public Builder partnerId(String partnerId) {
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

        private void validate() {
            if (isNullOrEmpty(this.partnerId)) {
                throw new KalturaClientConfigException(String.format("Invalid partner id: [%s]", this.partnerId));
            }
            if (isNullOrEmpty(this.userId)) {
                throw new KalturaClientConfigException(String.format("Invalid user id: [%s]", this.userId));
            }
            if (isNullOrEmpty(this.secret)) {
                throw new KalturaClientConfigException(String.format("Invalid secret: [%s]", this.secret));
            }
        }

        private boolean isNullOrEmpty(String input) {
            return (input == null || "".equals(input));
        }
    }
}
