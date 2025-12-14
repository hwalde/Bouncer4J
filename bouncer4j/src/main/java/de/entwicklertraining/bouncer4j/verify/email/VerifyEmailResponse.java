package de.entwicklertraining.bouncer4j.verify.email;

import de.entwicklertraining.api.base.ApiClient;
import de.entwicklertraining.bouncer4j.BouncerResponse;
import org.json.JSONObject;

import java.util.Optional;

/**
 * Neue Variante einer Response-Klasse, analog zu GptChatCompletionResponse.
 * Bietet Methoden, die die JSON-Felder in unsere Enums (EmailStatus, VerificationReason etc.) übersetzen.
 */
public final class VerifyEmailResponse extends BouncerResponse<VerifyEmailRequest> {

    // Neue Felder für domain, dns, account
    private final Domain domain;     // optional
    private final Dns dns;          // optional
    private final Account account;  // optional

    public VerifyEmailResponse(JSONObject json, VerifyEmailRequest request) {
        super(json, request);

        // Domain
        JSONObject domainJson = json.optJSONObject("domain");
        if (domainJson != null) {
            this.domain = Domain.fromJson(domainJson);
        } else {
            this.domain = null;
        }

        // DNS
        JSONObject dnsJson = json.optJSONObject("dns");
        if (dnsJson != null) {
            this.dns = Dns.fromJson(dnsJson);
        } else {
            this.dns = null;
        }

        // Account
        JSONObject accountJson = json.optJSONObject("account");
        if (accountJson != null) {
            this.account = Account.fromJson(accountJson);
        } else {
            this.account = null;
        }

        // Weiterführende Validierung: z. B. ob "email" oder "status" in jeder Response vorhanden sein müssen
        if (!json.has("email")) {
            throw new ApiClient.ApiResponseUnusableException("Missing required field 'email' in top-level response.");
        }
        if (!json.has("status")) {
            throw new ApiClient.ApiResponseUnusableException("Missing required field 'status' in top-level response.");
        }
        // reason kann in manchen Fällen fehlen, ist aber in den Dokus meist vorhanden.
        // Falls wir es als "required" ansehen, bitte einkommentieren:
        /*
        if (!getJson().has("reason")) {
            throw new ApiClient.ApiResponseUnusableException("Missing required field 'reason' in top-level response.");
        }
        */
    }

    /**
     * Gibt die verifizierte E-Mail-Adresse zurück.
     */
    public String getEmail() {
        return getJson().optString("email", null);
    }

    /**
     * Liefert den EmailStatus als Enum (z. B. DELIVERABLE, RISKY, etc.).
     */
    public EmailStatus getStatus() {
        String raw = getJson().optString("status", "unknown").toUpperCase();
        try {
            return EmailStatus.valueOf(raw);
        } catch (Exception e) {
            return EmailStatus.UNKNOWN;
        }
    }

    /**
     * Liefert den Grund (reason) als Enum.
     */
    public VerificationReason getReason() {
        String raw = getJson().optString("reason", "unknown").toUpperCase();
        try {
            return VerificationReason.valueOf(raw);
        } catch (Exception e) {
            return VerificationReason.UNKNOWN;
        }
    }

    /**
     * Ein numerischer Score, 0 < x < 100
     */
    public Integer getScore() {
        if (!getJson().has("score")) return null;
        return getJson().optInt("score", -1);
    }

    /**
     * Der Provider (z. B. "google.com"), laut Doku
     * kann er top-level stehen oder im domain-Objekt.
     * Falls Bouncer lieber "provider" top-level füllt, hier abrufbar:
     */
    public String getProvider() {
        return getJson().optString("provider", null);
    }

    /**
     * RetryAfter (z. B. "2022-11-24T09:55:09.029Z")
     */
    public String getRetryAfter() {
        return getJson().optString("retryAfter", null);
    }

    /**
     * toxic als yes/no/unknown in der alten Implementierung.
     * Gemäß Dokumentation kann hier aber jeder Wert stehen,
     * oder "unknown", "yes", "no" etc.
     * Wir interpretieren das jetzt mit YesNoUnknown.
     */
    public YesNoUnknown getToxic() {
        String raw = getJson().optString("toxic", "unknown");
        return YesNoUnknown.fromString(raw);
    }

    /**
     * toxicity (0..100)
     */
    public Integer getToxicity() {
        if (!getJson().has("toxicity")) return null;
        return getJson().optInt("toxicity", -1);
    }

    /**
     * Account-Daten (optional).
     * @return Optional<Account>
     */
    public Optional<Account> getAccount() {
        return Optional.ofNullable(account);
    }

    /**
     * Domain-Daten (optional).
     * @return Optional<Domain>
     */
    public Optional<Domain> getDomain() {
        return Optional.ofNullable(domain);
    }

    /**
     * DNS-Daten (optional).
     * @return Optional<Dns>
     */
    public Optional<Dns> getDns() {
        return Optional.ofNullable(dns);
    }

    /**
     * Convenience method to get the Account object directly (can be null).
     * @return Account object or null if not present
     */
    public Account getAccountObject() {
        return account;
    }

    /**
     * Convenience method to get the Domain object directly (can be null).
     * @return Domain object or null if not present
     */
    public Domain getDomainObject() {
        return domain;
    }
}
