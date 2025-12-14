package de.entwicklertraining.bouncer4j.batch;

import de.entwicklertraining.api.base.ApiClient;
import de.entwicklertraining.bouncer4j.BouncerResponse;
import org.json.JSONObject;

import java.util.Optional;

/**
 * Antwort auf:
 * GET /v1.1/email/verify/batch/{batchId}
 * (mit optional with-stats=true)
 *
 * Mögliche Felder (z.B.):
 * {
 *   "batchId": "4d5fdf6b5ee97c4dbbccbfe1",
 *   "created": "2023-03-26T15:27:26.713Z",
 *   "started": "2023-03-26T15:27:30.124Z",
 *   "completed": "2023-03-26T15:27:43.250Z",
 *   "status": "completed",
 *   "quantity": 2,
 *   "duplicates": 0,
 *   "credits": 2,
 *   "processed": 2,
 *   "stats": {
 *     "deliverable": 1,
 *     "risky": 0,
 *     "undeliverable": 1,
 *     "unknown": 0
 *   }
 * }
 */
public final class CheckBatchStatusResponse extends BouncerResponse<CheckBatchStatusRequest> {

    private final String batchId;
    private final String created;
    private final String started;
    private final String completed;
    private final String status;
    private final int quantity;
    private final int duplicates;
    private final Integer credits;    // optional
    private final Integer processed;  // optional
    private final BatchStats stats;   // optional

    public CheckBatchStatusResponse(JSONObject json, CheckBatchStatusRequest request) {
        super(json, request);

        JSONObject obj = json;

        // System.out.println("DEBUG CheckBatchStatusResponse JSON: " + obj);

        if (!obj.has("batchId")) {
            throw new ApiClient.ApiResponseUnusableException("Missing 'batchId' in CheckBatchStatusResponse");
        }
        this.batchId = obj.optString("batchId", null);

        if (!obj.has("created")) {
            throw new ApiClient.ApiResponseUnusableException("Missing 'created' in CheckBatchStatusResponse");
        }
        this.created = obj.optString("created", null);

        // started, completed, credits, processed, stats, etc. können optional sein
        this.started = obj.optString("started", null);
        this.completed = obj.optString("completed", null);

        if (!obj.has("status")) {
            throw new ApiClient.ApiResponseUnusableException("Missing 'status' in CheckBatchStatusResponse");
        }
        this.status = obj.optString("status", null);

        if (!obj.has("quantity")) {
            throw new ApiClient.ApiResponseUnusableException("Missing 'quantity' in CheckBatchStatusResponse");
        }
        this.quantity = obj.optInt("quantity", -1);

        if (!obj.has("duplicates")) {
            throw new ApiClient.ApiResponseUnusableException("Missing 'duplicates' in CheckBatchStatusResponse");
        }
        this.duplicates = obj.optInt("duplicates", -1);

        if (obj.has("credits")) {
            this.credits = obj.optInt("credits", -1);
        } else {
            this.credits = null;
        }

        if (obj.has("processed")) {
            this.processed = obj.optInt("processed", -1);
        } else {
            this.processed = null;
        }

        if (obj.has("stats")) {
            JSONObject statsObj = obj.getJSONObject("stats");
            this.stats = BatchStats.fromJson(statsObj);
        } else {
            this.stats = null;
        }
    }

    public String getBatchId() {
        return batchId;
    }

    public String getCreated() {
        return created;
    }

    public String getStarted() {
        return started;
    }

    public String getCompleted() {
        return completed;
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

    public Integer getCredits() {
        return credits;
    }

    public Integer getProcessed() {
        return processed;
    }

    public BatchStats getStats() {
        return stats;
    }

    /**
     * Returns the stats wrapped in an Optional, since stats can be null.
     * @return Optional containing the stats or empty if not available
     */
    public Optional<BatchStats> getStatsOptional() {
        return Optional.ofNullable(stats);
    }

    /**
     * Convenience method to get the total number of emails in the batch.
     * This is an alias for getQuantity().
     * @return the total number of emails in the batch
     */
    public int getTotal() {
        return quantity;
    }
}
