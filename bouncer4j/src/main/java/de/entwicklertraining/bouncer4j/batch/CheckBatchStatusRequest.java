package de.entwicklertraining.bouncer4j.batch;

import de.entwicklertraining.api.base.ApiRequestBuilderBase;
import de.entwicklertraining.bouncer4j.BouncerClient;
import de.entwicklertraining.bouncer4j.BouncerRequest;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Represents a request to check the status of a batch verification job.
 * This endpoint retrieves the current status and optionally statistics for a batch.
 */
public final class CheckBatchStatusRequest extends BouncerRequest<CheckBatchStatusResponse> {

    private final String batchId;
    private final boolean withStats;

    CheckBatchStatusRequest(Builder builder) {
        super(builder);
        this.batchId = builder.batchId;
        this.withStats = builder.withStats;
    }

    @Override
    public String getRelativeUrl() {
        StringBuilder sb = new StringBuilder("/v1.1/email/verify/batch");
        sb.append("/").append(URLEncoder.encode(batchId, StandardCharsets.UTF_8));
        if (withStats) {
            sb.append("?with-stats=true");
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
    public CheckBatchStatusResponse createResponse(String responseBody) {
        return new CheckBatchStatusResponse(new JSONObject(responseBody), this);
    }

    public static Builder builder(BouncerClient bouncerClient) {
        return new Builder(bouncerClient);
    }

    public static final class Builder extends ApiRequestBuilderBase<Builder, CheckBatchStatusRequest> {
        private final BouncerClient bouncerClient;
        private String batchId;
        private boolean withStats = false;

        public Builder(BouncerClient bouncerClient) {
            this.bouncerClient = bouncerClient;
        }

        public Builder batchId(String batchId) {
            this.batchId = batchId;
            return this;
        }

        public Builder withStats(boolean withStats) {
            this.withStats = withStats;
            return this;
        }

        @Override
        public CheckBatchStatusRequest build() {
            return new CheckBatchStatusRequest(this);
        }

        @Override
        public CheckBatchStatusResponse execute() {
            return this.bouncerClient.sendRequest(build());
        }

        @Override
        public CheckBatchStatusResponse executeWithExponentialBackoff() {
            return this.bouncerClient.sendRequestWithExponentialBackoff(build());
        }
    }
}
