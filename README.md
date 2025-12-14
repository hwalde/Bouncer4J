# Bouncer4J

A fluent Java API client library for [Bouncer](https://usebouncer.com), an email verification service that helps validate email addresses and domains for better deliverability and reputation management.

> **A word from the author**
>
> I created this library to provide a type-safe and convenient way to access the Bouncer email verification API from Java applications. The library builds on top of the lightweight [`api-base`](https://github.com/hwalde/api-base) library which handles HTTP communication, authentication, and exponential backoff. This implementation stays close to the raw Bouncer API while providing a fluent, builder-based interface that's familiar to Java developers.

## Features

- **Email Verification**: Validate individual email addresses for deliverability
- **Domain Verification**: Check domain validity, accept-all status, and disposable domains  
- **Batch Processing**: Efficiently verify multiple emails with async processing
- **Credit Management**: Monitor your API credit balance
- **Fluent Builder API**: Type-safe request builders for all endpoints
- **Automatic Retry**: Built-in exponential backoff for rate limits and server errors
- **Service Helper**: High-level service class with configurable safety rules

## Installation

Add the dependency from Maven Central:

```xml
<dependency>
    <groupId>de.entwicklertraining</groupId>
    <artifactId>bouncer4j</artifactId>
    <version>1.0.0</version>
</dependency>
```

For Gradle:
```gradle
implementation 'de.entwicklertraining:bouncer4j:1.0.0'
```

## Getting Started

### API Key Configuration

There are two ways to provide your Bouncer API key:

#### 1. Environment Variable (Recommended)
Set the `BOUNCER_API_KEY` environment variable:
```bash
export BOUNCER_API_KEY=your-api-key-here
```

Then create a client without parameters:
```java
BouncerClient client = new BouncerClient();
```

#### 2. Programmatic Configuration
Pass the API key directly:
```java
BouncerClient client = new BouncerClient("your-api-key-here");
```

Or use HTTP configuration for advanced scenarios:
```java
import de.entwicklertraining.api.base.ApiClientSettings;
import de.entwicklertraining.api.base.ApiHttpConfiguration;

BouncerClient client = new BouncerClient(
    ApiClientSettings.builder().build(),
    ApiHttpConfiguration.builder()
        .header("x-api-key", "your-api-key-here")
        .build()
);
```

## API Endpoints

### Check Credits
Monitor your API credit balance:
```java
CheckCreditsResponse response = client.credits()
    .execute();
    
System.out.println("Available credits: " + response.getCredits());
```

### Verify Email Address
Validate a single email address with detailed results:
```java
VerifyEmailResponse response = client.verify().email()
    .email("test@example.com")
    .timeout(10)  // Optional timeout in seconds
    .execute();

// Check deliverability status
EmailStatus status = response.getStatus(); // DELIVERABLE, RISKY, UNDELIVERABLE, UNKNOWN
System.out.println("Status: " + status);
System.out.println("Reason: " + response.getReason());

// Check domain properties
Domain domain = response.getDomainObject();
System.out.println("Disposable: " + domain.getDisposable());
System.out.println("Accept All: " + domain.getAcceptAll());
System.out.println("Free Provider: " + domain.getFree());

// Check account details
response.getAccount().ifPresent(account -> {
    System.out.println("Role Account: " + account.getRole());
    System.out.println("Disabled: " + account.getDisabled());
    System.out.println("Full Mailbox: " + account.getFullMailbox());
});
```

### Verify Domain
Check domain validity and email acceptance policies:
```java
VerifyDomainResponse response = client.verify().domain()
    .domain("example.com")
    .execute();

Domain domain = response.getDomainObject();
System.out.println("Domain: " + domain.getName());
System.out.println("Accept All: " + domain.getAcceptAll());
System.out.println("Disposable: " + domain.getDisposable());
System.out.println("Free Provider: " + domain.getFree());

// DNS information
response.getDns().ifPresent(dns -> {
    System.out.println("DNS Type: " + dns.getType());
    System.out.println("DNS Record: " + dns.getRecord());
});

// Provider and toxicity
System.out.println("Provider: " + response.getProvider());
System.out.println("Toxic: " + response.getToxic());
```

### Batch Email Verification
Process multiple email addresses efficiently:

#### Complete Batch Workflow
```java
// 1. Create batch
CreateBatchResponse createResponse = client.batch().create()
    .emails(List.of("email1@example.com", "email2@example.com", "email3@example.com"))
    .execute();

String batchId = createResponse.getBatchId();
System.out.println("Batch created: " + batchId);

// 2. Monitor status
CheckBatchStatusResponse statusResponse = client.batch().status(batchId)
    .withStats(true)  // Include statistics
    .execute();

System.out.println("Status: " + statusResponse.getStatus());
System.out.println("Processed: " + statusResponse.getProcessed() + "/" + statusResponse.getTotal());

// Display statistics if available
statusResponse.getStats().ifPresent(stats -> {
    System.out.println("Deliverable: " + stats.getDeliverable());
    System.out.println("Risky: " + stats.getRisky());
    System.out.println("Undeliverable: " + stats.getUndeliverable());
    System.out.println("Unknown: " + stats.getUnknown());
});

// 3. Download results when complete
DownloadBatchResultsResponse results = client.batch().download(batchId)
    .filter("all")  // Options: all, deliverable, risky, undeliverable, unknown
    .execute();

for (BatchResultItem item : results.getItems()) {
    System.out.println(item.getEmail() + ": " + item.getStatus() + " (" + item.getReason() + ")");
}

// 4. Clean up
DeleteBatchResponse deleteResponse = client.batch().delete(batchId)
    .execute();
```

## High-Level Service Helper

The `BouncerCheckService` provides a simplified interface with configurable safety rules:

```java
// Create service with custom configuration
BouncerCheckService service = new BouncerCheckService();
BouncerCheckConfig config = BouncerCheckConfig.standard()
    .setBlockDisposable(true)      // Block disposable email domains
    .setBlockAcceptAll(false)       // Allow accept-all domains
    .setBlockFullMailbox(true)      // Block full mailboxes
    .setMaxAllowedToxicity(3)       // Maximum toxicity score
    .setTreatUnknownAsUnsafe(true); // Treat unknown status as unsafe

// Check single email
EmailCheckResult result = service.checkSingleEmail("test@example.com", config);
System.out.println("Email: " + result.getEmail());
System.out.println("Reachable: " + result.isReachable());
System.out.println("Safe to send: " + result.isSafe());
if (!result.isSafe()) {
    System.out.println("Reason: " + result.getReasonNotSafe());
}

// Batch check with automatic polling and cleanup
List<String> emails = List.of(
    "valid@gmail.com",
    "disposable@mailinator.com",
    "invalid@nonexistent-domain.com"
);

List<EmailCheckResult> results = service.checkBatchEmails(emails, config);
for (EmailCheckResult r : results) {
    System.out.println(r);
}
```

## Advanced Features

### Exponential Backoff
All requests support automatic retry with exponential backoff:
```java
VerifyEmailResponse response = client.verify().email()
    .email("test@example.com")
    .executeWithExponentialBackoff();
```

### Request Inspection
Monitor outgoing requests with hooks:
```java
ApiClientSettings settings = ApiClientSettings.builder()
    .beforeSend(req -> {
        System.out.println("Sending: " + req.getHttpMethod() + " " + req.getRelativeUrl());
    })
    .build();

BouncerClient client = new BouncerClient(settings);
```

### Custom Base URL
Use a custom API endpoint:
```java
BouncerClient client = new BouncerClient(
    ApiClientSettings.builder().build(),
    null,  // Use default HTTP config (reads API key from environment)
    "https://custom-api.example.com"
);
```

## Error Handling

The library uses typed exceptions from the `ApiClient` base class:
- `ApiClient.HTTP_400_RequestRejectedException` - Invalid request format
- `ApiClient.HTTP_401_AuthorizationException` - Invalid or missing API key
- `ApiClient.HTTP_402_PaymentRequiredException` - Insufficient credits
- `ApiClient.HTTP_403_ForbiddenException` - Access forbidden
- `ApiClient.HTTP_404_NotFoundException` - Resource not found
- `ApiClient.HTTP_429_RateLimitOrQuotaException` - Rate limit exceeded (automatically retried)
- `ApiClient.HTTP_503_ServerUnavailableException` - Server unavailable (automatically retried)

Example error handling:
```java
try {
    VerifyEmailResponse response = client.verify().email()
        .email("test@example.com")
        .execute();
} catch (ApiClient.HTTP_402_PaymentRequiredException e) {
    System.err.println("Out of credits! Please add more credits to your account.");
} catch (ApiClient.HTTP_429_RateLimitOrQuotaException e) {
    System.err.println("Rate limit hit. Consider using executeWithExponentialBackoff().");
} catch (Exception e) {
    System.err.println("Unexpected error: " + e.getMessage());
}
```

## Project Structure

The library follows a clear structure:

* **`BouncerClient`** – Entry point for all API calls. Extends `ApiClient` from *api-base*
* **Request/Response classes** – Located in packages like `verify.email`, `verify.domain`, `batch`, and `credits`
* **Service layer** – `BouncerCheckService` provides high-level operations with safety rules
* **Configuration** – `BouncerCheckConfig` for customizing validation rules
* **Examples** – The `bouncer4j-examples` module contains complete working examples

## Examples

Complete working examples are available in the `bouncer4j-examples` module:
- `BouncerCreditsExample.java` - Check API credits
- `VerifyEmailExample.java` - Single email verification
- `VerifyDomainExample.java` - Domain verification  
- `BouncerBatchExample.java` - Batch processing workflow
- `BouncerServiceExample.java` - Using the service helper with safety rules

## Building

This project uses Maven. Compile the library and run examples with:

```bash
mvn clean package
```

## Requirements

- Java 21 or higher
- Maven or Gradle for dependency management

## License

Bouncer4J is distributed under the MIT License. See LICENSE file for details.

## Support

For issues, questions, or contributions:
- GitHub: https://github.com/hwalde/Bouncer4J
- Email: info@entwickler-training.de