package de.entwicklertraining.bouncer4j.batch;

import de.entwicklertraining.api.base.ApiRequestBuilderBase;
import de.entwicklertraining.bouncer4j.BouncerClient;
import de.entwicklertraining.bouncer4j.BouncerRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Represents a request to create a batch verification job.
 * This endpoint accepts multiple email addresses for batch processing.
 */
public final class CreateBatchRequest extends BouncerRequest<CreateBatchResponse> {

    private final List<String> emails;

    CreateBatchRequest(Builder builder) {
        super(builder);
        this.emails = builder.emails;
    }

    @Override
    public String getRelativeUrl() {
        return "/v1.1/email/verify/batch";
    }

    @Override
    public String getHttpMethod() {
        return "POST";
    }

    @Override
    public String getBody() {
        JSONArray array = new JSONArray();
        for (String email : emails) {
            JSONObject obj = new JSONObject();
            obj.put("email", email);
            array.put(obj);
        }
        return array.toString();
    }

    @Override
    public CreateBatchResponse createResponse(String responseBody) {
        return new CreateBatchResponse(new JSONObject(responseBody), this);
    }

    public static Builder builder(BouncerClient bouncerClient) {
        return new Builder(bouncerClient);
    }

    public static final class Builder extends ApiRequestBuilderBase<Builder, CreateBatchRequest> {
        private final BouncerClient bouncerClient;
        private List<String> emails;

        public Builder(BouncerClient bouncerClient) {
            this.bouncerClient = bouncerClient;
        }

        public Builder emails(List<String> emails) {
            this.emails = emails;
            return this;
        }

        public Builder emails(String... emails) {
            this.emails = List.of(emails);
            return this;
        }

        @Override
        public CreateBatchRequest build() {
            return new CreateBatchRequest(this);
        }

        @Override
        public CreateBatchResponse execute() {
            return this.bouncerClient.sendRequest(build());
        }

        @Override
        public CreateBatchResponse executeWithExponentialBackoff() {
            return this.bouncerClient.sendRequestWithExponentialBackoff(build());
        }
    }
}
