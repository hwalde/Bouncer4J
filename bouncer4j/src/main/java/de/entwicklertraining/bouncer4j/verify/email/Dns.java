package de.entwicklertraining.bouncer4j.verify.email;

import de.entwicklertraining.api.base.ApiClient;
import org.json.JSONObject;

/**
 * Enthält Informationen über den "dns"-Teil der Bouncer-Response.
 *
 * Pflichtfelder laut Dokumentation:
 * - type (String, z.B. "MX" oder "A")
 * - record (String) - *Kann fehlen, wenn type="unknown"*
 */
public final class Dns {

    private final DnsType type;
    private final String record; // Kann null sein

    private Dns(DnsType type, String record) { // DnsType statt String verwenden
        this.type = type;
        this.record = record;
    }

    public DnsType getType() {
        return type;
    }

    public String getRecord() {
        return record; // Kann null zurückgeben
    }

    public static Dns fromJson(JSONObject json) {
        if (!json.has("type")) {
            throw new ApiClient.ApiResponseUnusableException("Missing required field 'dns.type' in response.");
        }
        String typeStr = json.optString("type", "UNKNOWN").toUpperCase(); // Default zu UNKNOWN
        DnsType type;
        try {
            type = DnsType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            System.err.println("Warning: Unknown DnsType received: " + typeStr + ". Using UNKNOWN.");
            type = DnsType.UNKNOWN; // Fallback für unbekannte Typen
        }

        String record = json.optString("record", null); // record ist optional

        return new Dns(type, record);
    }
}