package de.entwicklertraining.bouncer4j.verify.email;

import de.entwicklertraining.api.base.ApiRequestBuilderBase;
import de.entwicklertraining.bouncer4j.BouncerClient;
import de.entwicklertraining.bouncer4j.BouncerRequest;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Represents a request to the Bouncer Email Verify API.
 * This endpoint verifies the deliverability of an email address.
 */
public final class VerifyEmailRequest extends BouncerRequest<VerifyEmailResponse> {

    private final String email;
    private final Integer timeout;

    VerifyEmailRequest(Builder builder) {
        super(builder);
        this.email = builder.email;
        this.timeout = builder.timeout;
    }

    @Override
    public String getRelativeUrl() {
        StringBuilder sb = new StringBuilder("/v1.1/email/verify");
        sb.append("?email=").append(URLEncoder.encode(email, StandardCharsets.UTF_8));
        if (timeout != null) {
            sb.append("&timeout=").append(timeout);
        }
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
    public VerifyEmailResponse createResponse(String responseBody) {
        return new VerifyEmailResponse(new JSONObject(responseBody), this);
    }

    public static Builder builder(BouncerClient bouncerClient) {
        return new Builder(bouncerClient);
    }

    public static final class Builder extends ApiRequestBuilderBase<Builder, VerifyEmailRequest> {
        private final BouncerClient bouncerClient;
        private String email;
        private Integer timeout;

        public Builder(BouncerClient bouncerClient) {
            this.bouncerClient = bouncerClient;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder timeout(Integer timeout) {
            this.timeout = timeout;
            return this;
        }

        @Override
        public VerifyEmailRequest build() {
            return new VerifyEmailRequest(this);
        }

        @Override
        public VerifyEmailResponse execute() {
            return this.bouncerClient.sendRequest(build());
        }

        @Override
        public VerifyEmailResponse executeWithExponentialBackoff() {
            return this.bouncerClient.sendRequestWithExponentialBackoff(build());
        }
    }
}
