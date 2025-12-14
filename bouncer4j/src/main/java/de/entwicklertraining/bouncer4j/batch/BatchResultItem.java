package de.entwicklertraining.bouncer4j.batch;

import de.entwicklertraining.api.base.ApiClient;
import de.entwicklertraining.bouncer4j.verify.email.Account;
import de.entwicklertraining.bouncer4j.verify.email.Dns;
import de.entwicklertraining.bouncer4j.verify.email.Domain;
import de.entwicklertraining.bouncer4j.verify.email.EmailStatus;
import de.entwicklertraining.bouncer4j.verify.email.VerificationReason;
import de.entwicklertraining.bouncer4j.verify.email.YesNoUnknown;
import org.json.JSONObject;

import java.util.Optional;

/**
 * Einzelnes Result-Objekt beim Download der Batch-Ergebnisse.
 * Strukturell analog zu den Feldern in VerifyEmailResponse,
 * nur eben pro Eintrag in der Array-Liste.
 */
public final class BatchResultItem {

    private final String email;
    private final EmailStatus status;
    private final VerificationReason reason;
    private final Domain domain;   // optional
    private final Dns dns;         // optional
    private final Account account; // optional
    private final String provider;
    private final Integer score;
    private final YesNoUnknown toxic;
    private final Integer toxicity;
    private final String retryAfter;
    private final String didYouMean; // optional laut Beispiel

    private BatchResultItem(
            String email,
            EmailStatus status,
            VerificationReason reason,
            Domain domain,
            Dns dns,
            Account account,
            String provider,
            Integer score,
            YesNoUnknown toxic,
            Integer toxicity,
            String retryAfter,
            String didYouMean
    ) {
        this.email = email;
        this.status = status;
        this.reason = reason;
        this.domain = domain;
        this.dns = dns;
        this.account = account;
        this.provider = provider;
        this.score = score;
        this.toxic = toxic;
        this.toxicity = toxicity;
        this.retryAfter = retryAfter;
        this.didYouMean = didYouMean;
    }

    public String getEmail() {
        return email;
    }

    public EmailStatus getStatus() {
        return status;
    }

    public VerificationReason getReason() {
        return reason;
    }

    public Optional<Domain> getDomain() {
        return Optional.ofNullable(domain);
    }

    public Optional<Dns> getDns() {
        return Optional.ofNullable(dns);
    }

    public Optional<Account> getAccount() {
        return Optional.ofNullable(account);
    }

    public String getProvider() {
        return provider;
    }

    public Integer getScore() {
        return score;
    }

    public YesNoUnknown getToxic() {
        return toxic;
    }

    public Integer getToxicity() {
        return toxicity;
    }

    public String getRetryAfter() {
        return retryAfter;
    }

    public String getDidYouMean() {
        return didYouMean;
    }

    public static BatchResultItem fromJson(JSONObject json) {
        if (!json.has("email")) {
            throw new ApiClient.ApiResponseUnusableException("Missing 'email' in batch result item");
        }
        String email = json.optString("email", null);

        // status
        String rawStatus = json.optString("status", "unknown").toUpperCase();
        EmailStatus status;
        try {
            status = EmailStatus.valueOf(rawStatus);
        } catch (Exception e) {
            status = EmailStatus.UNKNOWN;
        }

        // reason
        String rawReason = json.optString("reason", "unknown").toUpperCase();
        VerificationReason reason;
        try {
            reason = VerificationReason.valueOf(rawReason);
        } catch (Exception e) {
            reason = VerificationReason.UNKNOWN;
        }

        // domain
        Domain domain = null;
        if (json.has("domain") && json.optJSONObject("domain") != null) {
            domain = Domain.fromJson(json.getJSONObject("domain"));
        }

        // dns
        Dns dns = null;
        if (json.has("dns") && json.optJSONObject("dns") != null) {
            dns = Dns.fromJson(json.getJSONObject("dns"));
        }

        // account
        Account account = null;
        if (json.has("account") && json.optJSONObject("account") != null) {
            account = Account.fromJson(json.getJSONObject("account"));
        }

        // provider
        String provider = json.optString("provider", null);

        // score
        Integer score = null;
        if (json.has("score")) {
            score = json.optInt("score", -1);
        }

        // toxic
        String rawToxic = json.optString("toxic", "unknown");
        YesNoUnknown toxic = YesNoUnknown.fromString(rawToxic);

        // toxicity
        Integer toxicity = null;
        if (json.has("toxicity")) {
            toxicity = json.optInt("toxicity", -1);
        }

        // retryAfter
        String retryAfter = json.optString("retryAfter", null);

        // didYouMean
        String didYouMean = json.optString("didYouMean", null);

        return new BatchResultItem(
                email,
                status,
                reason,
                domain,
                dns,
                account,
                provider,
                score,
                toxic,
                toxicity,
                retryAfter,
                didYouMean
        );
    }
}
