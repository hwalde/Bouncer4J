package de.entwicklertraining.bouncer4j;

import de.entwicklertraining.api.base.ApiClient;
import de.entwicklertraining.api.base.ApiClientSettings;
import de.entwicklertraining.api.base.ApiHttpConfiguration;
import de.entwicklertraining.bouncer4j.batch.*;
import de.entwicklertraining.bouncer4j.credits.CheckCreditsRequest;
import de.entwicklertraining.bouncer4j.verify.domain.VerifyDomainRequest;
import de.entwicklertraining.bouncer4j.verify.email.VerifyEmailRequest;

/**
 * Client for the Bouncer email verification API.
 *
 * <p>The client automatically reads the API key from the BOUNCER_API_KEY environment variable
 * if not explicitly provided via {@link ApiHttpConfiguration}.
 *
 * <p>Error behavior:
 * <ul>
 *   <li>400 -&gt; throw ApiClient.HTTP_400_RequestRejectedException</li>
 *   <li>401 -&gt; throw ApiClient.HTTP_401_AuthorizationException</li>
 *   <li>402 -&gt; throw ApiClient.HTTP_402_PaymentRequiredException</li>
 *   <li>403 -&gt; throw ApiClient.HTTP_403_PermissionDeniedException</li>
 *   <li>404 -&gt; throw ApiClient.HTTP_404_NotFoundException</li>
 *   <li>429 -&gt; attempt exponential backoff; if still not resolved -&gt; throw ApiClient.HTTP_429_RateLimitOrQuotaException</li>
 *   <li>503 -&gt; attempt exponential backoff; if still not resolved -&gt; throw ApiClient.HTTP_503_ServerUnavailableException</li>
 * </ul>
 */
public final class BouncerClient extends ApiClient {

    private static final String DEFAULT_BASE_URL = "https://api.usebouncer.com";

    /**
     * Creates a new BouncerClient with default settings.
     * The API key is read from the BOUNCER_API_KEY environment variable.
     */
    public BouncerClient() {
        this(ApiClientSettings.builder().build(), (ApiHttpConfiguration) null, DEFAULT_BASE_URL);
    }

    /**
     * Creates a new BouncerClient with the specified API key.
     *
     * @param apiKey the Bouncer API key for authentication
     */
    public BouncerClient(String apiKey) {
        this(ApiClientSettings.builder().build(), createHttpConfigWithApiKey(apiKey), DEFAULT_BASE_URL);
    }

    /**
     * Creates a new BouncerClient with custom settings.
     * The API key is read from the BOUNCER_API_KEY environment variable.
     *
     * @param settings Client settings for retry behavior and timeouts
     */
    public BouncerClient(ApiClientSettings settings) {
        this(settings, (ApiHttpConfiguration) null, DEFAULT_BASE_URL);
    }

    /**
     * Creates a new BouncerClient with custom settings and HTTP configuration.
     *
     * @param settings Client settings for retry behavior and timeouts
     * @param httpConfig HTTP configuration including authentication headers
     */
    public BouncerClient(ApiClientSettings settings, ApiHttpConfiguration httpConfig) {
        this(settings, httpConfig, DEFAULT_BASE_URL);
    }

    /**
     * Creates a new BouncerClient with custom settings, HTTP configuration, and base URL.
     *
     * @param settings Client settings for retry behavior and timeouts
     * @param httpConfig HTTP configuration including authentication headers (can be null)
     * @param customBaseUrl Custom base URL for the API
     */
    public BouncerClient(ApiClientSettings settings, ApiHttpConfiguration httpConfig, String customBaseUrl) {
        super(settings, buildHttpConfig(httpConfig));

        setBaseUrl(customBaseUrl);

        // Register status code exceptions
        registerStatusCodeException(400, HTTP_400_RequestRejectedException.class, "Invalid request (HTTP 400):", false);
        registerStatusCodeException(401, HTTP_401_AuthorizationException.class, "Authentication failed (HTTP 401):", false);
        registerStatusCodeException(402, HTTP_402_PaymentRequiredException.class, "Payment required (HTTP 402):", false);
        registerStatusCodeException(403, HTTP_403_PermissionDeniedException.class, "Forbidden (HTTP 403):", false);
        registerStatusCodeException(404, HTTP_404_NotFoundException.class, "Not found (HTTP 404):", false);
        registerStatusCodeException(429, HTTP_429_RateLimitOrQuotaException.class, "Rate limit or quota exceeded (HTTP 429):", true);
        registerStatusCodeException(503, HTTP_503_ServerUnavailableException.class, "Server overloaded (HTTP 503):", true);
    }

    /**
     * Creates an HTTP configuration with the specified API key.
     */
    private static ApiHttpConfiguration createHttpConfigWithApiKey(String apiKey) {
        return ApiHttpConfiguration.builder()
            .header("x-api-key", apiKey)
            .build();
    }

    /**
     * Builds the HTTP configuration, adding the API key from environment variable if not already set.
     */
    private static ApiHttpConfiguration buildHttpConfig(ApiHttpConfiguration existingConfig) {
        // Check if we already have an x-api-key header
        if (existingConfig != null && existingConfig.getGlobalHeaders().containsKey("x-api-key")) {
            return existingConfig;
        }

        // Try to get API key from environment variable
        String apiKey = System.getenv("BOUNCER_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            // No API key available - return existing config or empty config
            // The request will fail with 401, which is appropriate
            return existingConfig != null ? existingConfig : new ApiHttpConfiguration();
        }

        // Build new config with API key
        ApiHttpConfiguration.Builder builder = existingConfig != null
            ? existingConfig.toBuilder()
            : ApiHttpConfiguration.builder();

        return builder
            .header("x-api-key", apiKey)
            .build();
    }

    // Credits endpoint
    public CheckCreditsRequest.Builder credits() {
        return CheckCreditsRequest.builder(this);
    }

    // Verify endpoints
    public BouncerVerify verify() {
        return new BouncerVerify(this);
    }

    // Batch endpoints
    public BouncerBatch batch() {
        return new BouncerBatch(this);
    }

    /**
     * Provides access to email and domain verification endpoints.
     * Accessible via client.verify().email() or client.verify().domain()
     */
    public static class BouncerVerify {
        private final BouncerClient client;

        public BouncerVerify(BouncerClient client) {
            this.client = client;
        }

        public VerifyEmailRequest.Builder email() {
            return VerifyEmailRequest.builder(client);
        }

        public VerifyDomainRequest.Builder domain() {
            return VerifyDomainRequest.builder(client);
        }
    }

    /**
     * Provides access to batch verification endpoints for processing multiple email addresses.
     * Supports create, status check, download results, finish, and delete operations.
     */
    public static class BouncerBatch {
        private final BouncerClient client;

        public BouncerBatch(BouncerClient client) {
            this.client = client;
        }

        public CreateBatchRequest.Builder create() {
            return CreateBatchRequest.builder(client);
        }

        public CheckBatchStatusRequest.Builder status(String batchId) {
            return CheckBatchStatusRequest.builder(client).batchId(batchId);
        }

        public DownloadBatchResultsRequest.Builder download(String batchId) {
            return DownloadBatchResultsRequest.builder(client).batchId(batchId);
        }

        public FinishBatchRequest.Builder finish(String batchId) {
            return FinishBatchRequest.builder(client).batchId(batchId);
        }

        public DeleteBatchRequest.Builder delete(String batchId) {
            return DeleteBatchRequest.builder(client).batchId(batchId);
        }
    }
}