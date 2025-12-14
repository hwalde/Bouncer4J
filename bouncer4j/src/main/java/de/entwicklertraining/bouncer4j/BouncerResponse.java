package de.entwicklertraining.bouncer4j;

import de.entwicklertraining.api.base.ApiResponse;
import org.json.JSONObject;

/**
 * Base class for all Bouncer API responses.
 * Extends ApiResponse to provide common functionality for all Bouncer responses.
 */
public abstract class BouncerResponse<T extends BouncerRequest<?>> extends ApiResponse<T> {

    protected final JSONObject json;
    private final T request;

    protected BouncerResponse(JSONObject json, T request) {
        super(request);
        this.json = json;
        this.request = request;
    }

    public JSONObject getJson() {
        return json;
    }

    public T getRequest() {
        return request;
    }
}
