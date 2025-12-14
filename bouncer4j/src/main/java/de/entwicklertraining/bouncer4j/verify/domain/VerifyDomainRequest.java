package de.entwicklertraining.bouncer4j.verify.domain;

import de.entwicklertraining.api.base.ApiRequestBuilderBase;
import de.entwicklertraining.bouncer4j.BouncerClient;
import de.entwicklertraining.bouncer4j.BouncerRequest;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Represents a request to the Bouncer Domain Verify API.
 * This endpoint verifies domain-level information for email validation.
 */
public final class VerifyDomainRequest extends BouncerRequest<VerifyDomainResponse> {

    private final String domain;

    VerifyDomainRequest(Builder builder) {
        super(builder);
        this.domain = builder.domain;
    }

    @Override
    public String getRelativeUrl() {
        StringBuilder sb = new StringBuilder("/v1.1/domain");
        sb.append("?domain=").append(URLEncoder.encode(domain, StandardCharsets.UTF_8));
        return sb.toString();
    }

    @Override
    public String getHttpMethod() {
        return "GET";
    }

    @Override
    public String getBody() {
        // GET request doesn't have a body
        return null;
    }

    @Override
    public VerifyDomainResponse createResponse(String responseBody) {
        return new VerifyDomainResponse(new JSONObject(responseBody), this);
    }

    public static Builder builder(BouncerClient bouncerClient) {
        return new Builder(bouncerClient);
    }

    public static final class Builder extends ApiRequestBuilderBase<Builder, VerifyDomainRequest> {
        private final BouncerClient bouncerClient;
        private String domain;

        public Builder(BouncerClient bouncerClient) {
            this.bouncerClient = bouncerClient;
        }

        public Builder domain(String domain) {
            this.domain = domain;
            return this;
        }

        @Override
        public VerifyDomainRequest build() {
            return new VerifyDomainRequest(this);
        }

        @Override
        public VerifyDomainResponse execute() {
            return this.bouncerClient.sendRequest(build());
        }

        @Override
        public VerifyDomainResponse executeWithExponentialBackoff() {
            return this.bouncerClient.sendRequestWithExponentialBackoff(build());
        }
    }
}
