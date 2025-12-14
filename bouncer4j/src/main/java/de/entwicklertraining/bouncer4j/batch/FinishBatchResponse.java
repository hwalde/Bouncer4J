package de.entwicklertraining.bouncer4j.batch;

import de.entwicklertraining.bouncer4j.BouncerResponse;
import org.json.JSONObject;

/**
 * Antwort auf:
 * POST /v1.1/email/verify/batch/{batchId}/finish
 *
 * Laut Doku kann man 202 ({}), 400 usw. erhalten.
 */
public final class FinishBatchResponse extends BouncerResponse<FinishBatchRequest> {

    public FinishBatchResponse(JSONObject json, FinishBatchRequest request) {
        super(json, request);
        // Keine Pflichtfelder laut Doku, kann leer sein oder Fehler.
    }
}
