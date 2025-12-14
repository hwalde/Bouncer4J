package de.entwicklertraining.bouncer4j.batch;

import de.entwicklertraining.api.base.ApiRequestBuilderBase;
import de.entwicklertraining.bouncer4j.BouncerClient;
import de.entwicklertraining.bouncer4j.BouncerRequest;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Represents a request to delete a batch verification job.
 * This endpoint removes the batch and all associated data.
 */
public final class DeleteBatchRequest extends BouncerRequest<DeleteBatchResponse> {

    private final String batchId;

    DeleteBatchRequest(Builder builder) {
        super(builder);
        this.batchId = builder.batchId;
    }

    @Override
    public String getRelativeUrl() {
        StringBuilder sb = new StringBuilder("/v1.1/email/verify/batch");
        sb.append("/").append(URLEncoder.encode(batchId, StandardCharsets.UTF_8));
        return sb.toString();
    }

    @Override
    public String getHttpMethod() {
        return "DELETE";
    }

    @Override
    public String getBody() {
        // DELETE request doesn't have a body
        return null;
    }

    @Override
    public DeleteBatchResponse createResponse(String responseBody) {
        return new DeleteBatchResponse(responseBody, this);
    }

    public static Builder builder(BouncerClient bouncerClient) {
        return new Builder(bouncerClient);
    }

    public static final class Builder extends ApiRequestBuilderBase<Builder, DeleteBatchRequest> {
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
        public DeleteBatchRequest build() {
            return new DeleteBatchRequest(this);
        }

        @Override
        public DeleteBatchResponse execute() {
            return this.bouncerClient.sendRequest(build());
        }

        @Override
        public DeleteBatchResponse executeWithExponentialBackoff() {
            return this.bouncerClient.sendRequestWithExponentialBackoff(build());
        }
    }
}
