package de.entwicklertraining.bouncer4j.batch;

import de.entwicklertraining.api.base.ApiClient;
import de.entwicklertraining.bouncer4j.BouncerResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Antwort auf:
 * GET /v1.1/email/verify/batch/{batchId}/download?download=all
 * <p>
 * Erfolgsfall: Array von Objekten (siehe Single Email Verification Schema).
 */
public final class DownloadBatchResultsResponse extends BouncerResponse<DownloadBatchResultsRequest> {

    private final List<BatchResultItem> items;

    public DownloadBatchResultsResponse(String jsonBody, DownloadBatchResultsRequest request) {
        // Rufe den Super-Konstruktor mit einem leeren, gültigen JSON-Objekt auf,
        // um den Fehler "JSONObject text must begin with '{'" zu vermeiden,
        // da der eigentliche jsonBody ein Array ist.
        super(new JSONObject(), request);

        // Nun verarbeite den *tatsächlichen* jsonBody, der das Array enthält.
        Object parsed = null;
        try {
            // Verwende JSONTokener, um sowohl Objekte als auch Arrays zu erkennen.
            parsed = new JSONTokener(jsonBody).nextValue();
        } catch (org.json.JSONException e) { // Fange JSONException statt generischem Exception
            throw new ApiClient.ApiResponseUnusableException("Invalid JSON in DownloadBatchResultsResponse: " + e.getMessage(), e);
        }

        if (parsed instanceof JSONObject obj) {
            // Es könnte ein Fehlerobjekt von Bouncer sein (z.B. 404 oder anderer Fehler statt Array)
            // Prüfen, ob es typische Fehlerfelder enthält
            if (obj.has("status") || obj.has("error") || obj.has("message")) {
                // BouncerClient sollte bereits Exceptions für Statuscodes != 2xx werfen.
                // Aber falls wir hier landen, ist es wahrscheinlich ein Fehler.
                // Wir geben eine leere Liste zurück, aber loggen den Fehler.
                System.err.println("Received error JSON object instead of results array: " + obj);
                this.items = Collections.emptyList();
                // Optional: Hier könnte man auch eine Exception werfen, je nach gewünschtem Verhalten
                // throw new ApiClient.ApiResponseUnusableException("Expected JSON array, but received error object: " + obj.toString());
            } else {
                // Unerwartete Struktur (Objekt statt Array, aber kein klarer Fehler)
                throw new ApiClient.ApiResponseUnusableException("Expected JSON array, got unexpected JSON object: " + obj);
            }
        } else if (parsed instanceof JSONArray arr) {
            // Dies ist der erwartete Fall für erfolgreiche Downloads
            List<BatchResultItem> tmp = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject itemJson = arr.optJSONObject(i); // optJSONObject verwenden für mehr Robustheit
                if (itemJson != null) {
                    try {
                        tmp.add(BatchResultItem.fromJson(itemJson));
                    } catch (ApiClient.ApiResponseUnusableException e) {
                        // Logge Fehler beim Parsen eines einzelnen Items, aber fahre fort
                        System.err.println("Error parsing batch result item at index " + i + ": " + e.getMessage() + " - JSON: " + itemJson);
                        // Optional: Das fehlerhafte Item überspringen oder null hinzufügen etc.
                    }
                } else {
                    System.err.println("Warning: Found null or non-object element in batch results array at index " + i);
                }
            }
            this.items = Collections.unmodifiableList(tmp);
        } else {
            // Weder Objekt noch Array - unerwarteter Typ
            throw new ApiClient.ApiResponseUnusableException("Unknown JSON structure in DownloadBatchResultsResponse. Expected Array or Object, got: " + (parsed != null ? parsed.getClass().getName() : "null"));
        }
    }

    public List<BatchResultItem> getItems() {
        return items;
    }
}