Wichtig: Aktualisiere AGENTS.md nach jedem Task.
Wichtig: Aktualisiere die README.md nach jedem Task nur wenn die Informationen darin veraltet sind

# AGENTS.md

This file provides guidance to Coding Agents when working with code in this repository.

## Project Overview

Bouncer4J is a Java library that provides a fluent API wrapper for the Bouncer API. It's built on top of the `api-base` library and focuses on staying as close to the raw Bouncer API as possible while providing type safety and convenience.

## Build System

This is a Maven multi-module project with Java 21 as the target version.

### Common Commands

```bash
# Build the entire project
mvn package

# Compile only
mvn compile

# Clean build
mvn clean package

# Run tests (when available)
mvn test

# Skip deployment for examples module (already configured)
mvn deploy  # examples module will be automatically skipped
```

### Module Structure

- **Root project**: `bouncer4j-project` - Parent POM with shared configuration
- **Core library**: `bouncer4j` - Main library implementation  
- **Examples**: `bouncer4j-examples` - Example usage (not deployed to Maven Central)

## Architecture

### Core Components

**BouncerClient** (`bouncer4j/src/main/java/de/entwicklertraining/bouncer4j/BouncerClient.java:23`)
- Main entry point extending `ApiClient` from api-base
- Handles HTTP communication, authentication, and error handling
- Provides fluent access via `chat()`, `models()`, and `user()` methods
- Automatically reads API key from `BOUNCER_API_KEY` environment variable
- Registers specific HTTP error handlers with exponential backoff for 429/503

**Request/Response Pattern**
- `BouncerRequest` - Abstract base for all API requests
- `BouncerResponse` - Abstract base for all API responses with JSON parsing
- Each endpoint has its own Request/Response pair in dedicated packages:
  - `chat.completion` - Chat completion API
  - `models` - Models listing API  
  - `user.balance` - User balance API

**Tool System**
- `BouncerToolDefinition` - Defines callable functions
- `BouncerToolsCallback` - Handles tool execution
- `BouncerToolCallContext` - Provides context during tool calls
- `BouncerJsonSchema` - Schema definition for structured outputs

**Token Services**
- `BouncerTokenService` - Token counting utilities using DJL tokenizer

### Key Dependencies

- `api-base` (1.0.4) - HTTP client foundation with retry logic
- Jackson (2.19.0) - JSON processing
- JSON-java (20240303) - Additional JSON utilities
- DJL (0.34.0) - Deep Java Library for tokenization
- JUnit 5 + Mockito + AssertJ + WireMock - Testing stack

### API Authentication

The client uses Bearer token authentication. API key resolution:
1. Explicit key via `ApiClientSettings.setBearerAuthenticationKey()`
2. Falls back to `BOUNCER_API_KEY` environment variable

### Error Handling

Comprehensive HTTP error mapping with specific exceptions:
- 400 → `HTTP_400_RequestRejectedException`
- 401 → `HTTP_401_AuthorizationException` 
- 402 → `HTTP_402_PaymentRequiredException`
- 422 → `HTTP_422_UnprocessableEntityException`
- 429 → `HTTP_429_RateLimitOrQuotaException` (with retry)
- 500 → `HTTP_500_ServerErrorException`
- 503 → `HTTP_503_ServerUnavailableException` (with retry)

### Extending the Library

To add new endpoints:
1. Create a request class extending `BouncerRequest`
2. Create a response class extending `BouncerResponse`  
3. Add a builder method to `BouncerClient`
4. Follow the existing package structure (`endpoint.subfeature`)

## Development Notes

- Java 21 required for compilation
- Uses Maven Central for dependencies
- Examples module demonstrates all major features
- No test files currently present in the repository
- Project uses MIT license
- GPG signing enabled for releases

## Maintenance
- Run `mvn package` to ensure the project builds. There are no tests.
- Keep this AGENTS.md file in sync with repository changes.
- Only update README.md if the information becomes outdated.

### Recent Changes
* Added Models and User Balance endpoints to AGENTS.md and README.md documentation

Wichtig: Aktualisiere AGENTS.md nach jedem Task.
Wichtig: Aktualisiere die README.md nach jedem Task nur wenn die Informationen darin veraltet sind