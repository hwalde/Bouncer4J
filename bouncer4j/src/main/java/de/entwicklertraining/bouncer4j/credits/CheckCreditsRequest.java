package de.entwicklertraining.bouncer4j.credits;

import de.entwicklertraining.api.base.ApiRequestBuilderBase;
import de.entwicklertraining.bouncer4j.BouncerClient;
import de.entwicklertraining.bouncer4j.BouncerRequest;
import org.json.JSONObject;

/**
 * Represents a request to the Bouncer Credits API.
 * This endpoint returns the current credit balance for the API key.
 */
public final class CheckCreditsRequest extends BouncerRequest<CheckCreditsResponse> {

    CheckCreditsRequest(Builder builder) {
        super(builder);
    }

    @Override
    public String getRelativeUrl() {
        return "/v1.1/credits";
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
    public CheckCreditsResponse createResponse(String responseBody) {
        return new CheckCreditsResponse(new JSONObject(responseBody), this);
    }

    public static Builder builder(BouncerClient bouncerClient) {
        return new Builder(bouncerClient);
    }

    public static final class Builder extends ApiRequestBuilderBase<Builder, CheckCreditsRequest> {
        private final BouncerClient bouncerClient;

        public Builder(BouncerClient bouncerClient) {
            this.bouncerClient = bouncerClient;
        }

        @Override
        public CheckCreditsRequest build() {
            return new CheckCreditsRequest(this);
        }

        @Override
        public CheckCreditsResponse execute() {
            return this.bouncerClient.sendRequest(build());
        }

        @Override
        public CheckCreditsResponse executeWithExponentialBackoff() {
            return this.bouncerClient.sendRequestWithExponentialBackoff(build());
        }
    }
}