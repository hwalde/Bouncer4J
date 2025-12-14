package de.entwicklertraining.bouncer4j.batch;

import de.entwicklertraining.bouncer4j.BouncerResponse;
import org.json.JSONObject;

/**
 * Represents a response from deleting a batch verification job.
 * Success cases: 204 No Content (empty body) or 200 OK (empty body or {}).
 * Error cases: JSON object with error message.
 */
public final class DeleteBatchResponse extends BouncerResponse<DeleteBatchRequest> {

    public DeleteBatchResponse(String jsonBody, DeleteBatchRequest request) {
        // Handle empty response bodies from DELETE requests (204 No Content)
        super(createJsonFromString(jsonBody), request);
    }

    private static JSONObject createJsonFromString(String jsonBody) {
        if (isJsonBodyEmptyOrNull(jsonBody)) {
            return new JSONObject(); // Empty JSON object for empty responses
        }
        return new JSONObject(jsonBody);
    }

    private static boolean isJsonBodyEmptyOrNull(String jsonBody) {
        return jsonBody == null || jsonBody.trim().isEmpty();
    }
}