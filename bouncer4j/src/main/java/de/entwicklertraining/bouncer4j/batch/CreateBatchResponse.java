package de.entwicklertraining.bouncer4j.batch;

import de.entwicklertraining.api.base.ApiClient;
import de.entwicklertraining.bouncer4j.BouncerResponse;
import org.json.JSONObject;

/**
 * Repräsentiert die Antwort auf:
 * POST /v1.1/email/verify/batch
 *
 * Beispiel-Antwort 200:
 * {
 *   "batchId": "4d5fdf6b5ee97c4dbbccbfe1",
 *   "created": "2023-03-26T18:08:15.033Z",
 *   "status": "queued",
 *   "quantity": 2,
 *   "duplicates": 0
 * }
 */
public final class CreateBatchResponse extends BouncerResponse<CreateBatchRequest> {

    private final String batchId;
    private final String created;
    private final String status;
    private final int quantity;
    private final int duplicates;

    public CreateBatchResponse(JSONObject json, CreateBatchRequest request) {
        super(json, request);

        JSONObject obj = json;

        if (!obj.has("batchId")) {
            throw new ApiClient.ApiResponseUnusableException("Missing 'batchId' in CreateBatchResponse");
        }
        this.batchId = obj.optString("batchId", null);

        if (!obj.has("created")) {
            throw new ApiClient.ApiResponseUnusableException("Missing 'created' in CreateBatchResponse");
        }
        this.created = obj.optString("created", null);

        if (!obj.has("status")) {
            throw new ApiClient.ApiResponseUnusableException("Missing 'status' in CreateBatchResponse");
        }
        this.status = obj.optString("status", null);

        if (!obj.has("quantity")) {
            throw new ApiClient.ApiResponseUnusableException("Missing 'quantity' in CreateBatchResponse");
        }
        this.quantity = obj.optInt("quantity", -1);

        if (!obj.has("duplicates")) {
            throw new ApiClient.ApiResponseUnusableException("Missing 'duplicates' in CreateBatchResponse");
        }
        this.duplicates = obj.optInt("duplicates", -1);
    }

    public String getBatchId() {
        return batchId;
    }

    public String getCreated() {
        return created;
    }

    public String getStatus() {
        return status;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getDuplicates() {
        return duplicates;
    }
}
