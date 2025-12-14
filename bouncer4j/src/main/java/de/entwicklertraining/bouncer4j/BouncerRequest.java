package de.entwicklertraining.bouncer4j;

import de.entwicklertraining.api.base.ApiRequest;
import de.entwicklertraining.api.base.ApiRequestBuilderBase;

/**
 * Base class for all Bouncer API requests.
 * Extends ApiRequest to provide common functionality for all Bouncer requests.
 */
public abstract class BouncerRequest<T extends BouncerResponse<?>> extends ApiRequest<T> {

    protected <Y extends ApiRequestBuilderBase<?, ?>> BouncerRequest(Y builder) {
        super(builder);
    }

    /**
     * Returns the relative URL for this request (without the base URL).
     * @return the relative URL path
     */
    public abstract String getRelativeUrl();

    /**
     * Returns the HTTP method for this request.
     * @return the HTTP method (GET, POST, DELETE, etc.)
     */
    public abstract String getHttpMethod();

    /**
     * Returns the request body as a string, or null for requests without a body.
     * @return the request body or null
     */
    public abstract String getBody();

    /**
     * Returns the content type for this request.
     * @return always "application/json" for Bouncer API
     */
    public String getContentType() {
        return "application/json";
    }

    /**
     * Creates a response object from the response body.
     * @param responseBody the response body as a string
     * @return the response object
     */
    public abstract T createResponse(String responseBody);
}