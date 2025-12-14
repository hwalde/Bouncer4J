package de.entwicklertraining.bouncer4j.examples;

import de.entwicklertraining.bouncer4j.BouncerClient;
import de.entwicklertraining.bouncer4j.credits.CheckCreditsResponse;

/**
 * A simple example demonstrating how to check your Bouncer API credit balance.
 */
public class BouncerCreditsExample {

    public static void main(String[] args) {
        // Create a client - reads API key from BOUNCER_API_KEY environment variable
        BouncerClient client = new BouncerClient();
        
        try {
            // Check available credits
            CheckCreditsResponse response = client.credits()
                    .execute();
            
            // Display the results
            System.out.println("Available Credits: " + response.getCredits());
            
            // You can also access the raw JSON response
            System.out.println("\nRaw JSON Response:");
            System.out.println(response.getJson().toString(2));
            
        } catch (Exception e) {
            System.err.println("Error checking credits: " + e.getMessage());
            e.printStackTrace();
        }
    }
}