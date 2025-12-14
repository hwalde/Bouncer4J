package de.entwicklertraining.bouncer4j.examples;

import de.entwicklertraining.bouncer4j.BouncerClient;
import de.entwicklertraining.bouncer4j.exceptions.BouncerException;
import de.entwicklertraining.bouncer4j.verify.domain.VerifyDomainResponse;
import de.entwicklertraining.bouncer4j.verify.email.YesNoUnknown;

/**
 * Example demonstrating domain verification using the Bouncer API.
 *
 * <p>Usage: java VerifyDomainExample [domain]
 * <p>If no domain is provided, uses example.com as a placeholder.
 */
public class VerifyDomainExample {

    public static void main(String[] args) {
        // Create a client - reads API key from BOUNCER_API_KEY environment variable
        BouncerClient client = new BouncerClient();

        // Use domain from command line argument or default placeholder
        String domainToCheck = args.length > 0 ? args[0] : "example.com";

        try {
            // Verify a domain
            VerifyDomainResponse response = client.verify().domain()
                    .domain(domainToCheck)
                    .execute();

            // Display domain information
            System.out.println("Domain: " + response.getDomainObject().getName());
            System.out.println("AcceptAll: " + response.getDomainObject().getAcceptAll());
            System.out.println("Disposable: " + response.getDomainObject().getDisposable());
            System.out.println("Free: " + response.getDomainObject().getFree());

            // DNS information (if available)
            response.getDns().ifPresent(dns -> {
                System.out.println("DNS Type: " + dns.getType());
                System.out.println("DNS Record: " + dns.getRecord());
            });

            // Provider information
            System.out.println("Provider: " + response.getProvider());

            // Toxicity check
            YesNoUnknown toxic = response.getToxic();
            System.out.println("Toxic: " + toxic);

        } catch (BouncerException ex) {
            System.err.println("Bouncer API error: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("General error: " + ex.getMessage());
        }
    }
}