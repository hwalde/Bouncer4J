package de.entwicklertraining.bouncer4j.examples;

import de.entwicklertraining.bouncer4j.BouncerClient;
import de.entwicklertraining.bouncer4j.verify.email.VerifyEmailResponse;

/**
 * Example demonstrating the use of BouncerClient for email verification.
 * Shows how to call the verify email endpoint and access all response fields.
 *
 * <p>Usage: java BouncerServiceExample [email]
 * <p>If no email is provided, uses test@example.com as a placeholder.
 */
public class BouncerServiceExample {
    public static void main(String[] args) {
        // Create a client - reads API key from BOUNCER_API_KEY environment variable
        BouncerClient client = new BouncerClient();

        // Use email from command line argument or default placeholder
        String email = args.length > 0 ? args[0] : "test@example.com";

        try {
            // Call the verification endpoint via the client
            VerifyEmailResponse response = client.verify().email()
                    .email(email)
                    .timeout(10)  // Optional timeout
                    .execute();

            // Raw JSON output for debugging
            System.out.println("Raw JSON Response:");
            System.out.println(response.getJson());
            System.out.println();

            // Basic fields
            System.out.println("=== Basic Information ===");
            System.out.println("Email: " + response.getEmail());
            System.out.println("Status: " + response.getStatus());
            System.out.println("Reason: " + response.getReason());

            // Domain information
            System.out.println("\n=== Domain Information ===");
            response.getDomain().ifPresent(domain -> {
                System.out.println("Domain Name: " + domain.getName());
                System.out.println("Accept All: " + domain.getAcceptAll());
                System.out.println("Disposable: " + domain.getDisposable());
                System.out.println("Free: " + domain.getFree());
                System.out.println("Provider (domain): " + domain.getProvider());
            });

            // Top-level provider
            System.out.println("\n=== Provider Information ===");
            System.out.println("Provider (top-level): " + response.getProvider());

            // DNS information
            System.out.println("\n=== DNS Information ===");
            response.getDns().ifPresent(dns -> {
                System.out.println("DNS Type: " + dns.getType());
                System.out.println("DNS Record: " + dns.getRecord());
            });

            // Account information
            System.out.println("\n=== Account Information ===");
            response.getAccount().ifPresent(account -> {
                System.out.println("Account disabled: " + account.getDisabled());
                System.out.println("Full mailbox: " + account.getFullMailbox());
                System.out.println("Role: " + account.getRole());
                System.out.println("Is role address: " + account.isRoleAddress());
            });

            // Scoring and toxicity
            System.out.println("\n=== Scoring Information ===");
            System.out.println("Score: " + response.getScore());
            System.out.println("Toxic: " + response.getToxic());
            System.out.println("Toxicity: " + response.getToxicity());
            System.out.println("Retry After: " + response.getRetryAfter());

        } catch (Exception ex) {
            // Handle various exceptions (e.g., ApiClient exceptions, network errors)
            System.err.println("Error verifying email: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}