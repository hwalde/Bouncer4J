package de.entwicklertraining.bouncer4j.batch;

import de.entwicklertraining.api.base.ApiRequestBuilderBase;
import de.entwicklertraining.bouncer4j.BouncerClient;
import de.entwicklertraining.bouncer4j.BouncerRequest;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Represents a request to finish a batch verification job.
 * This endpoint signals that no more emails will be added to the batch.
 */
public final class FinishBatchRequest extends BouncerRequest<FinishBatchResponse> {

    private final String batchId;

    FinishBatchRequest(Builder builder) {
        super(builder);
        this.batchId = builder.batchId;
    }

    @Override
    public String getRelativeUrl() {
        StringBuilder sb = new StringBuilder("/v1.1/email/verify/batch");
        sb.append("/").append(URLEncoder.encode(batchId, StandardCharsets.UTF_8));
        sb.append("/finish");
        return sb.toString();
    }

    @Override
    public String getHttpMethod() {
        return "POST";
    }

    @Override
    public String getBody() {
        // POST request with empty body
        return "{}";
    }

    @Override
    public FinishBatchResponse createResponse(String responseBody) {
        return new FinishBatchResponse(new JSONObject(responseBody), this);
    }

    public static Builder builder(BouncerClient bouncerClient) {
        return new Builder(bouncerClient);
    }

    public static final class Builder extends ApiRequestBuilderBase<Builder, FinishBatchRequest> {
        private final BouncerClient bouncerClient;
        private String batchId;

        public Builder(BouncerClient bouncerClient) {
            this.bouncerClient = bouncerClient;
        }

        public Builder batchId(String batchId) {
            this.batchId = batchId;
            return this;
        }

        @Override
        public FinishBatchRequest build() {
            return new FinishBatchRequest(this);
        }

        @Override
        public FinishBatchResponse execute() {
            return this.bouncerClient.sendRequest(build());
        }

        @Override
        public FinishBatchResponse executeWithExponentialBackoff() {
            return this.bouncerClient.sendRequestWithExponentialBackoff(build());
        }
    }
}
