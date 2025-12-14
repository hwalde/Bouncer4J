package de.entwicklertraining.bouncer4j.examples;

import de.entwicklertraining.api.base.ApiHttpConfiguration;
import de.entwicklertraining.bouncer4j.BouncerClient;
import de.entwicklertraining.bouncer4j.verify.email.VerifyEmailResponse;

/**
 * A simple example demonstrating email verification using the Bouncer API.
 *
 * <p>Usage: java VerifyEmailExample [email]
 * <p>If no email is provided, uses test@example.com as a placeholder.
 * <p>The API key is read from the BOUNCER_API_KEY environment variable,
 * or can be provided via ApiHttpConfiguration.
 */
public class VerifyEmailExample {
    public static void main(String[] args) {
        // Method 1: Using environment variable BOUNCER_API_KEY (recommended)
        BouncerClient client = new BouncerClient();

        // Method 2: Using ApiHttpConfiguration to provide API key explicitly
        // BouncerClient client = new BouncerClient(
        //     ApiClientSettings.builder().build(),
        //     ApiHttpConfiguration.builder()
        //         .header("x-api-key", "your-api-key-here")
        //         .build()
        // );

        // Use email from command line argument or default placeholder
        String email = args.length > 0 ? args[0] : "test@example.com";

        try {
            // Verify an email address
            VerifyEmailResponse response = client.verify().email()
                    .email(email)
                    .timeout(10)  // Optional timeout in seconds
                    .execute();

            // Display verification results
            System.out.println("Email: " + response.getEmail());
            System.out.println("Status: " + response.getStatus());
            System.out.println("Reason: " + response.getReason());
            
            // Domain information (check if domain exists first)
            if (response.getDomainObject() != null) {
                System.out.println("Accept All: " + response.getDomainObject().getAcceptAll());
                System.out.println("Disposable: " + response.getDomainObject().getDisposable());
                System.out.println("Free: " + response.getDomainObject().getFree());
            }
            
            // DNS information
            response.getDns().ifPresent(dns -> {
                System.out.println("\nDNS Information:");
                System.out.println("  Type: " + dns.getType());
                System.out.println("  Record: " + dns.getRecord());
            });

            // Account details (check if account exists first)
            if (response.getAccountObject() != null) {
                System.out.println("\nAccount Information:");
                System.out.println("  Role: " + response.getAccountObject().getRole());
                System.out.println("  Disabled: " + response.getAccountObject().getDisabled());
                System.out.println("  Full Mail Box: " + response.getAccountObject().getFullMailbox());
            }
            
            // Raw JSON for debugging
            System.out.println("\nRaw JSON Response:");
            System.out.println(response.getJson().toString(2));
            
        } catch (Exception e) {
            System.err.println("Error verifying email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}