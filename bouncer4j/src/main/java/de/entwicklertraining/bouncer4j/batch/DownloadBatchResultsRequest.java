package de.entwicklertraining.bouncer4j.batch;

import de.entwicklertraining.api.base.ApiRequestBuilderBase;
import de.entwicklertraining.bouncer4j.BouncerClient;
import de.entwicklertraining.bouncer4j.BouncerRequest;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Represents a request to download batch verification results.
 * This endpoint retrieves the verification results for a completed batch,
 * optionally filtered by status (all, deliverable, risky, undeliverable, unknown).
 */
public final class DownloadBatchResultsRequest extends BouncerRequest<DownloadBatchResultsResponse> {

    private final String batchId;
    private final String downloadFilter;

    DownloadBatchResultsRequest(Builder builder) {
        super(builder);
        this.batchId = builder.batchId;
        this.downloadFilter = builder.downloadFilter;
    }

    @Override
    public String getRelativeUrl() {
        StringBuilder sb = new StringBuilder("/v1.1/email/verify/batch");
        sb.append("/").append(URLEncoder.encode(batchId, StandardCharsets.UTF_8));
        sb.append("/download");
        if (downloadFilter != null) {
            sb.append("?download=").append(URLEncoder.encode(downloadFilter, StandardCharsets.UTF_8));
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
    public DownloadBatchResultsResponse createResponse(String responseBody) {
        return new DownloadBatchResultsResponse(responseBody, this);
    }

    public static Builder builder(BouncerClient bouncerClient) {
        return new Builder(bouncerClient);
    }

    public static final class Builder extends ApiRequestBuilderBase<Builder, DownloadBatchResultsRequest> {
        private final BouncerClient bouncerClient;
        private String batchId;
        private String downloadFilter = "all";

        public Builder(BouncerClient bouncerClient) {
            this.bouncerClient = bouncerClient;
        }

        public Builder batchId(String batchId) {
            this.batchId = batchId;
            return this;
        }

        public Builder filter(String filter) {
            this.downloadFilter = filter;
            return this;
        }

        @Override
        public DownloadBatchResultsRequest build() {
            return new DownloadBatchResultsRequest(this);
        }

        @Override
        public DownloadBatchResultsResponse execute() {
            return this.bouncerClient.sendRequest(build());
        }

        @Override
        public DownloadBatchResultsResponse executeWithExponentialBackoff() {
            return this.bouncerClient.sendRequestWithExponentialBackoff(build());
        }
    }
}
