package de.entwicklertraining.bouncer4j.credits;

import de.entwicklertraining.bouncer4j.BouncerResponse;
import org.json.JSONObject;

/**
 * Represents a response from the Bouncer Credits API.
 * Provides methods to access the current credit balance.
 */
public final class CheckCreditsResponse extends BouncerResponse<CheckCreditsRequest> {

    /**
     * Constructs a CheckCreditsResponse.
     *
     * @param json    The raw JSON response object.
     * @param request The original request that led to this response.
     */
    public CheckCreditsResponse(JSONObject json, CheckCreditsRequest request) {
        super(json, request);
    }

    /**
     * Gets the number of available credits.
     * @return The credit balance, or -1 if not present.
     */
    public int getCredits() {
        return getJson().optInt("credits", -1);
    }
}