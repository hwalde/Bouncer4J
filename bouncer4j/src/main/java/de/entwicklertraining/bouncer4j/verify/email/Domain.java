package de.entwicklertraining.bouncer4j.verify.email;

import de.entwicklertraining.api.base.ApiClient;
import org.json.JSONObject;

/**
 * Enthält Informationen über den "domain"-Teil der Bouncer-Response.
 * 
 * Pflichtfelder laut Dokumentation:
 *   - name (String, required)
 *   - acceptAll (yes/no/unknown)
 *   - disposable (yes/no/unknown)
 *   - free (yes/no/unknown)
 */
public final class Domain {

    private final String name;
    private final YesNoUnknown acceptAll;
    private final YesNoUnknown disposable;
    private final YesNoUnknown free;
    private final String provider; // optional laut Dokumentation

    private Domain(String name,
                   YesNoUnknown acceptAll,
                   YesNoUnknown disposable,
                   YesNoUnknown free,
                   String provider) {
        this.name = name;
        this.acceptAll = acceptAll;
        this.disposable = disposable;
        this.free = free;
        this.provider = provider;
    }

    public String getName() {
        return name;
    }

    public YesNoUnknown getAcceptAll() {
        return acceptAll;
    }

    public YesNoUnknown getDisposable() {
        return disposable;
    }

    public YesNoUnknown getFree() {
        return free;
    }

    public String getProvider() {
        return provider;
    }

    /**
     * Parst das Domain-JSON aus der Bouncer-Response. 
     * Prüft auf Pflichtfelder, wirft ApiClient.ApiResponseUnusableException, wenn etwas fehlt.
     */
    public static Domain fromJson(JSONObject json) {
        if (!json.has("name")) {
            throw new ApiClient.ApiResponseUnusableException("Missing required field 'domain.name' in response.");
        }
        String name = json.optString("name", null);

        // acceptAll/disposable/free sind nicht immer 100% explizit, 
        // aber laut Doku "required" -> wenn fehlt => Exception
        if (!json.has("acceptAll")) {
            throw new ApiClient.ApiResponseUnusableException("Missing required field 'domain.acceptAll' in response.");
        }
        YesNoUnknown acceptAll = YesNoUnknown.fromString(json.optString("acceptAll", null));

        if (!json.has("disposable")) {
            throw new ApiClient.ApiResponseUnusableException("Missing required field 'domain.disposable' in response.");
        }
        YesNoUnknown disposable = YesNoUnknown.fromString(json.optString("disposable", null));

        if (!json.has("free")) {
            throw new ApiClient.ApiResponseUnusableException("Missing required field 'domain.free' in response.");
        }
        YesNoUnknown free = YesNoUnknown.fromString(json.optString("free", null));

        // optional: provider
        String provider = json.optString("provider", null);

        return new Domain(name, acceptAll, disposable, free, provider);
    }
}
