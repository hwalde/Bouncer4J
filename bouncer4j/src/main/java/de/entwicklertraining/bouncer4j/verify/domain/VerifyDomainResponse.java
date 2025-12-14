package de.entwicklertraining.bouncer4j.verify.domain;

import de.entwicklertraining.api.base.ApiClient;
import de.entwicklertraining.bouncer4j.BouncerResponse;
import de.entwicklertraining.bouncer4j.verify.email.Dns;
import de.entwicklertraining.bouncer4j.verify.email.Domain;
import de.entwicklertraining.bouncer4j.verify.email.YesNoUnknown;
import org.json.JSONObject;

import java.util.Optional;

/**
 * Response-Klasse für den Bouncer Domain Verify-Endpoint (GET /v1.1/domain).
 *
 * Erwartete Felder laut Dokumentation:
 *   {
 *     "domain": {
 *       "name": "usebouncer.com",
 *       "acceptAll": "no",
 *       "disposable": "no",
 *       "free": "no"
 *     },
 *     "dns": {
 *       "type": "MX",
 *       "record": "aspmx.l.google.com."
 *     },
 *     "provider": "google.com",
 *     "toxic": "unknown"
 *   }
 */
public final class VerifyDomainResponse extends BouncerResponse<VerifyDomainRequest> {

    private final Domain domain;   // Pflicht laut Dokumentation
    private final Dns dns;         // optional
    private final String provider; // optional
    private final YesNoUnknown toxic;

    public VerifyDomainResponse(JSONObject json, VerifyDomainRequest request) {
        super(json, request);

        // domain (Pflicht)
        JSONObject domainJson = json.optJSONObject("domain");
        if (domainJson == null) {
            throw new ApiClient.ApiResponseUnusableException("Missing required field 'domain' in top-level response.");
        }
        this.domain = Domain.fromJson(domainJson);

        // dns (optional)
        JSONObject dnsJson = json.optJSONObject("dns");
        if (dnsJson != null) {
            this.dns = Dns.fromJson(dnsJson);
        } else {
            this.dns = null;
        }

        // provider (optional)
        this.provider = json.optString("provider", null);

        // toxic (optional, default = unknown)
        String rawToxic = json.optString("toxic", "unknown");
        this.toxic = YesNoUnknown.fromString(rawToxic);
    }

    /**
     * Enthält Informationen über das angefragte Domain-Objekt.
     */
    public Domain getDomainObject() {
        return domain;
    }

    /**
     * DNS-Daten (optional).
     */
    public Optional<Dns> getDns() {
        return Optional.ofNullable(dns);
    }

    /**
     * Name des Infrastrukturanbieters (z.B. "google.com"), falls vorhanden.
     */
    public String getProvider() {
        return provider;
    }

    /**
     * Kennzeichnet die "Toxic"-Eigenschaft. yes/no/unknown.
     */
    public YesNoUnknown getToxic() {
        return toxic;
    }
}
