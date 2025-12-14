package de.entwicklertraining.bouncer4j.examples;

import de.entwicklertraining.bouncer4j.BouncerClient;
import de.entwicklertraining.bouncer4j.batch.*;

import java.util.Arrays;
import java.util.List;

/**
 * Example demonstrating batch email verification using the Bouncer API.
 * Shows how to create, monitor, download results, and delete a batch.
 *
 * <p>Usage: java BouncerBatchExample [email1] [email2] [email3] ...
 * <p>If no emails are provided, uses placeholder emails.
 */
public class BouncerBatchExample {

    public static void main(String[] args) {
        // Create a client - reads API key from BOUNCER_API_KEY environment variable
        BouncerClient client = new BouncerClient();

        // Use emails from command line arguments or default placeholders
        List<String> emails = args.length > 0
                ? Arrays.asList(args)
                : List.of("test@example.com", "invalid@nonexistent-domain-12345.com", "contact@example.org");

        try {
            // 1) Create a new batch with emails to verify
            System.out.println("Creating batch with " + emails.size() + " emails...");
            CreateBatchResponse createResponse = client.batch().create()
                    .emails(emails)
                    .execute();
            
            String batchId = createResponse.getBatchId();
            System.out.println("Batch created with ID: " + batchId);
            System.out.println("Initial Status: " + createResponse.getStatus());

            // 2) Check batch status (with statistics)
            System.out.println("\nChecking batch status...");
            CheckBatchStatusResponse statusResponse = client.batch().status(batchId)
                    .withStats(true)
                    .execute();
            System.out.println("Current Status: " + statusResponse.getStatus());
            System.out.println("Processed: " + statusResponse.getProcessed() + "/" + statusResponse.getTotal());

            // 3) Wait for batch to complete (poll status until completed)
            System.out.println("\nWaiting for batch to complete...");
            CheckBatchStatusResponse finalStatus;
            int maxAttempts = 30;
            int attempt = 0;
            do {
                Thread.sleep(2000);  // Wait 2 seconds between polls
                finalStatus = client.batch().status(batchId)
                        .withStats(true)
                        .execute();
                System.out.println("  Status: " + finalStatus.getStatus() +
                        " (" + finalStatus.getProcessed() + "/" + finalStatus.getTotal() + ")");
                attempt++;
            } while (!"completed".equalsIgnoreCase(finalStatus.getStatus()) && attempt < maxAttempts);

            // 4) Check final status
            System.out.println("\nFinal Status: " + finalStatus.getStatus());
            System.out.println("Credits Used: " + finalStatus.getCredits());
            
            // Display statistics if available
            finalStatus.getStatsOptional().ifPresent(stats -> {
                System.out.println("\nBatch Statistics:");
                System.out.println("  Deliverable: " + stats.getDeliverable());
                System.out.println("  Risky: " + stats.getRisky());
                System.out.println("  Undeliverable: " + stats.getUndeliverable());
                System.out.println("  Unknown: " + stats.getUnknown());
            });

            // 5) Download verification results
            System.out.println("\nDownloading batch results...");
            DownloadBatchResultsResponse resultsResponse = client.batch().download(batchId)
                    .filter("all")  // Options: all, deliverable, risky, undeliverable, unknown
                    .execute();
            
            System.out.println("Results count: " + resultsResponse.getItems().size());
            System.out.println("\nVerification Results:");
            for (BatchResultItem item : resultsResponse.getItems()) {
                System.out.println(String.format("  - %s: %s (reason: %s)", 
                    item.getEmail(), 
                    item.getStatus(), 
                    item.getReason()));
            }

            // 6) Clean up - delete the batch
            System.out.println("\nDeleting batch...");
            DeleteBatchResponse deleteResponse = client.batch().delete(batchId).execute();
            System.out.println("Batch deleted. Response: " + deleteResponse.getJson());

        } catch (InterruptedException e) {
            System.err.println("Thread interrupted: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error during batch processing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}