package de.entwicklertraining.bouncer4j;

import de.entwicklertraining.bouncer4j.batch.*;
import de.entwicklertraining.bouncer4j.exceptions.BouncerException;
import de.entwicklertraining.bouncer4j.verify.email.*;

import java.util.*;

/**
 * Service for checking email addresses with Bouncer
 * to evaluate their reachability and safety for sending (reputation).
 */
public class BouncerCheckService {

    private final BouncerClient bouncerClient;
    private static final int BATCH_POLL_INTERVAL_MS = 5000; // 5 seconds
    private static final int MAX_BATCH_POLL_ATTEMPTS = 24; // Max 2 minutes wait

    public BouncerCheckService() {
        this.bouncerClient = new BouncerClient();
    }

    public BouncerCheckService(BouncerClient client) {
        this.bouncerClient = client;
    }

    /**
     * Checks a single email address.
     *
     * @param email  The email address to check.
     * @param config The configuration for security evaluation.
     * @return The check result.
     */
    public EmailCheckResult checkSingleEmail(String email, BouncerCheckConfig config) {
        Objects.requireNonNull(email, "Input email cannot be null");
        Objects.requireNonNull(config, "Input config cannot be null");
        try {
            VerifyEmailResponse response = bouncerClient.verify().email()
                    .email(email)
                    // Optional: Set timeout for individual request if needed
                    // .timeout(15)
                    .execute();

            return evaluateBouncerResult(
                    response.getEmail(),
                    response.getStatus(),
                    response.getReason(),
                    response.getDomain().orElse(null),
                    response.getAccount().orElse(null),
                    response.getToxicity(),
                    config);

        } catch (BouncerException e) {
            // API error or invalid response
            System.err.println("Bouncer API error for email " + email + ": " + e.getMessage());
            return new EmailCheckResult(email, false, false, "API Error: " + e.getMessage());
        } catch (Exception e) {
            // Other unexpected errors
            System.err.println("Unexpected error checking email " + email + ": " + e.getMessage());
            return new EmailCheckResult(email, false, false, "Unexpected Error: " + e.getMessage());
        }
    }

    /**
     * Checks a list of email addresses using batch processing.
     *
     * @param emails The list of email addresses to check. Must not contain null elements.
     * @param config The configuration for security evaluation.
     * @return A list of check results. The order may not match the input
     *         as duplicates might be removed by Bouncer. Use the email address for mapping.
     *         For emails not processed in the batch (e.g., API error), an error result is returned.
     */
    public List<EmailCheckResult> checkBatchEmails(List<String> emails, BouncerCheckConfig config) {
        Objects.requireNonNull(emails, "Input email list cannot be null");
        Objects.requireNonNull(config, "Input config cannot be null");
        if (emails.isEmpty()) {
            return new ArrayList<>();
        }

        // Explicit check for null elements in the list
        if (emails.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Input email list contains null elements!");
        }

        // Unique emails for processing and map initialization
        List<String> distinctEmails = emails.stream().distinct().toList();

        // Alternative map initialization to avoid toMap collector
        Map<String, EmailCheckResult> resultMap = new HashMap<>();
        for (String email : distinctEmails) {
            resultMap.put(email, null); // Placeholder
        }

        String batchId = null;
        try {
            // Send only unique emails to the batch API
            CreateBatchResponse createResponse = bouncerClient.batch().create()
                    .emails(distinctEmails)
                    .execute();
            batchId = createResponse.getBatchId();
            System.out.println("Batch created with ID: " + batchId + ", Status: " + createResponse.getStatus() + " for " + distinctEmails.size() + " distinct emails.");

            boolean completed = pollBatchCompletion(batchId);
            if (!completed) {
                String errorMsg = "Batch " + batchId + " did not complete within the timeout.";
                System.err.println(errorMsg);
                updateResultsWithError(resultMap, distinctEmails, "Batch Timeout"); // Update only unique ones
                return new ArrayList<>(resultMap.values());
            }

            // Download batch results
            DownloadBatchResultsResponse resultsResponse;
            try {
                resultsResponse = bouncerClient.batch().download(batchId)
                        .filter("all")
                        .execute();
            } catch (BouncerException | org.json.JSONException e) {
                System.err.println("Error downloading batch results for batch " + batchId + ": " + e.getMessage());
                throw new BouncerException("Failed to download batch results: " + e.getMessage(), e);
            }

            for (BatchResultItem item : resultsResponse.getItems()) {
                // Check if the email from the result was in our original list
                if (resultMap.containsKey(item.getEmail())) {
                    EmailCheckResult result = evaluateBouncerResult(
                            item.getEmail(),
                            item.getStatus(),
                            item.getReason(),
                            item.getDomain().orElse(null),
                            item.getAccount().orElse(null),
                            item.getToxicity(),
                            config);
                    resultMap.put(item.getEmail(), result);
                } else {
                    System.err.println("Warning: Received result for email not in the distinct input list: " + item.getEmail());
                }
            }

            // Check if all unique emails have a result
            for (String email : distinctEmails) {
                if (resultMap.get(email) == null) {
                    resultMap.put(email, new EmailCheckResult(email, false, false, "Email not found in Bouncer batch results"));
                    System.err.println("Warning: No result found for email in batch: " + email);
                }
            }

        } catch (BouncerException e) {
            System.err.println("Bouncer API error during batch processing (Batch ID: " + batchId + "): " + e.getMessage());
            updateResultsWithError(resultMap, distinctEmails, "API Error: " + e.getMessage()); // Update only unique ones
        } catch (Exception e) { // Including InterruptedException from polling
            System.err.println("Unexpected error during batch processing (Batch ID: " + batchId + "): " + e.getMessage());
            updateResultsWithError(resultMap, distinctEmails, "Unexpected Error: " + e.getMessage()); // Update only unique ones
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt(); // Restore interrupt status
            }
        } finally {
            if (batchId != null) {
                try {
                    System.out.println("Deleting batch " + batchId);
                    bouncerClient.batch().delete(batchId).execute();
                } catch (BouncerException e) {
                    System.err.println("Failed to delete batch " + batchId + ": " + e.getMessage());
                }
            }
        }

        // Ensure results are returned in order of *unique* input (optional, but often desired)
        // Or simply return the map values as before. Maintaining current behavior:
        return new ArrayList<>(resultMap.values());
    }

    /**
     * Helper method to poll batch status until completion.
     * @param batchId The batch ID.
     * @return true if batch completed successfully, false on timeout or error status.
     * @throws InterruptedException If thread is interrupted while waiting.
     * @throws BouncerException If an API error occurs during polling.
     */
    private boolean pollBatchCompletion(String batchId) throws InterruptedException, BouncerException {
        for (int attempt = 0; attempt < MAX_BATCH_POLL_ATTEMPTS; attempt++) {
            CheckBatchStatusResponse statusResponse = bouncerClient.batch().status(batchId).execute();
            String status = statusResponse.getStatus();
            // Defensive programming: check for null status
            if (status == null) {
                System.err.println("Warning: Received null status for Batch " + batchId + ". Assuming not completed.");
                Thread.sleep(BATCH_POLL_INTERVAL_MS); // Wait anyway
                continue;
            }
            Integer processed = statusResponse.getProcessed(); // Can be null
            Integer quantity = statusResponse.getQuantity();    // Should not be null, but check for safety
            System.out.println("Polling Batch " + batchId
                    + ", Status: " + status
                    + ", Processed: " + (processed != null ? processed : "?")
                    + "/" + (quantity != null ? quantity : "?"));

            if ("completed".equalsIgnoreCase(status)) {
                return true;
            }
            if ("failed".equalsIgnoreCase(status)) {
                System.err.println("Batch " + batchId + " failed.");
                return false; // Batch failed
            }
            // Other status (queued, processing) -> continue polling
            Thread.sleep(BATCH_POLL_INTERVAL_MS);
        }
        return false; // Timeout
    }

    /**
     * Centralized logic to evaluate a Bouncer result (from single or batch API).
     */
    private EmailCheckResult evaluateBouncerResult(
            String email,
            EmailStatus status,
            VerificationReason reason,
            Domain domain,
            Account account,
            Integer toxicity, // Can be null
            BouncerCheckConfig config) {
        // Defensive null-checks for critical input parameters
        Objects.requireNonNull(email, "Email cannot be null in evaluateBouncerResult");
        Objects.requireNonNull(status, "Status cannot be null in evaluateBouncerResult");
        // Reason can be null but will be parsed to UNKNOWN by enum
        // domain, account, toxicity can be null

        VerificationReason evalReason = (reason != null) ? reason : VerificationReason.UNKNOWN;

        boolean isReachable = (status == EmailStatus.DELIVERABLE || status == EmailStatus.RISKY);

        boolean isSafe = false; // Default: unsafe
        String reasonIfNotSafe = null;

        // Check unsafe criteria
        if (status == EmailStatus.UNDELIVERABLE) {
            reasonIfNotSafe = "Undeliverable (" + evalReason + ")";
        } else if (config.isTreatUnknownAsUnsafe() && status == EmailStatus.UNKNOWN) {
            reasonIfNotSafe = "Unknown Status (" + evalReason + ")";
        } else if (toxicity != null && toxicity > config.getMaxAllowedToxicity()) {
            // Toxicity score is 0-based in API (0..5?), but config is max *allowed* score
            reasonIfNotSafe = "High Toxicity Score (" + toxicity + " > " + config.getMaxAllowedToxicity() + ")";
        } else if (status == EmailStatus.RISKY) {
            // Check specific 'risky' reasons
            if (config.isBlockFullMailbox() && account != null && account.getFullMailbox() == YesNoUnknown.YES) {
                reasonIfNotSafe = "Risky (Full Mailbox)";
            } else if (config.isBlockDisposable() && domain != null && domain.getDisposable() == YesNoUnknown.YES) {
                reasonIfNotSafe = "Risky (Disposable Domain)";
            } else if (config.isBlockAcceptAll() && domain != null && domain.getAcceptAll() == YesNoUnknown.YES) {
                reasonIfNotSafe = "Risky (Accept All Domain)";
            }
            // If no specific risky reason leads to blocking AND status is risky:
            // Logic below handles this case (considered safe if not explicitly blocked).
        }

        // Decision on 'isSafe':
        // Safe if status is DELIVERABLE AND no other blocking reason was found.
        // Safe if status is RISKY AND none of the applicable 'risky' blocking rules (FullMailbox, Disposable, AcceptAll) were activated.
        // Otherwise unsafe.
        if (reasonIfNotSafe == null) { // Only if no blocking reason found so far
            if (status == EmailStatus.DELIVERABLE) {
                isSafe = true;
            } else if (status == EmailStatus.RISKY) {
                // If we're here, status was RISKY but NONE of the above risk-specific
                // blocking rules (based on config) applied. So we consider it safe.
                isSafe = true;
            }
            // UNKNOWN case is covered by config.isTreatUnknownAsUnsafe() above.
            // UNDELIVERABLE case is covered above.
        }

        // If isSafe is still false but reasonIfNotSafe is still null (shouldn't happen after above logic, but for safety):
        if (!isSafe && reasonIfNotSafe == null) {
            reasonIfNotSafe = "Considered unsafe (Status: " + status + (evalReason != VerificationReason.UNKNOWN ? ", Reason: " + evalReason : "") + ")";
        }

        return new EmailCheckResult(email, isReachable, isSafe, isSafe ? null : reasonIfNotSafe);
    }

    /**
     * Fills all entries in the result map that don't have a result yet (value is null)
     * with an error result.
     * @param resultMap The map with email as key and result as value.
     * @param emailsToCheck The list of emails whose results should be updated.
     * @param errorMessage The error message to store in the result.
     */
    private void updateResultsWithError(Map<String, EmailCheckResult> resultMap, List<String> emailsToCheck, String errorMessage) {
        for (String email : emailsToCheck) {
            // Only update if entry exists and value is still null
            if (resultMap.containsKey(email) && resultMap.get(email) == null) {
                resultMap.put(email, new EmailCheckResult(email, false, false, errorMessage));
            }
            // Optional: warning if an email is not in the map (shouldn't happen due to pre-fill)
            else if (!resultMap.containsKey(email)) {
                System.err.println("Warning in updateResultsWithError: Email " + email + " not found in resultMap.");
            }
        }
    }

    // --- Example usage ---
    public static void main(String[] args) {
        BouncerCheckService service = new BouncerCheckService();
        BouncerCheckConfig config = BouncerCheckConfig.standard()
                // Example: Allow AcceptAll domains
                .setBlockAcceptAll(false)
                // Example: Set maximum toxicity to 2 (stricter)
                .setMaxAllowedToxicity(2);

        // Example single check
        String email1 = "info@software-quality-services.de"; // Example valid
        String email2 = "invalid-email-syntax"; // Example invalid
        String email3 = "temporary@mailinator.com"; // Example disposable
        String email4 = "unknown@thisshouldtimeoutprobablyifnotsetproperly.com"; // Example unknown/timeout?

        System.out.println("--- Single Checks ---");
        //System.out.println(service.checkSingleEmail(email1, config)); // Temporarily disabled
        //System.out.println(service.checkSingleEmail(email2, config)); // Temporarily disabled
        //System.out.println(service.checkSingleEmail(email3, config)); // Temporarily disabled
        // System.out.println(service.checkSingleEmail(email4, config)); // Can take time

        // Example batch check
        List<String> batchEmails = List.of(
                /*email1,
                email2,
                email3,*/
                "anothergoodone@gmail.com", // More valid ones
                "test@acceptall-domain-example.com", // Example AcceptAll (now allowed due to config)
                "bounce@invalid-domain-that-does-not-exist-kjhgd.com" // Example Invalid Domain
        );

        System.out.println("\n--- Batch Check ---");
        try {
            List<EmailCheckResult> batchResults = service.checkBatchEmails(batchEmails, config);
            batchResults.forEach(System.out::println);
        } catch (IllegalArgumentException e) {
            System.err.println("Error starting batch check: " + e.getMessage());
        } catch (BouncerException e) { // Catch BouncerException here to prevent main program crash
            System.err.println("Batch check failed due to Bouncer API error: " + e.getMessage());
            // Optional: e.printStackTrace();
        }
    }
}