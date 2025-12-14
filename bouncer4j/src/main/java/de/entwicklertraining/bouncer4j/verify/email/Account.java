package de.entwicklertraining.bouncer4j.verify.email;

import de.entwicklertraining.api.base.ApiClient;
import org.json.JSONObject;

/**
 * Enthält Informationen über den "account"-Teil der Bouncer-Response.
 *
 * Pflichtfelder laut Dokumentation:
 *   - disabled: yes/no/unknown
 *   - fullMailbox: yes/no/unknown
 *   - role: yes/no/unknown
 */
public final class Account {

    private final YesNoUnknown disabled;
    private final YesNoUnknown fullMailbox;
    private final YesNoUnknown role;

    private Account(YesNoUnknown disabled,
                    YesNoUnknown fullMailbox,
                    YesNoUnknown role) {
        this.disabled = disabled;
        this.fullMailbox = fullMailbox;
        this.role = role;
    }

    public YesNoUnknown getDisabled() {
        return disabled;
    }

    public YesNoUnknown getFullMailbox() {
        return fullMailbox;
    }

    public YesNoUnknown getRole() {
        return role;
    }

    public boolean isRoleAddress() {
        return role == YesNoUnknown.YES;
    }

    public static Account fromJson(JSONObject json) {
        // disabled
        if (!json.has("disabled")) {
            throw new ApiClient.ApiResponseUnusableException("Missing required field 'account.disabled' in response.");
        }
        YesNoUnknown disabled = YesNoUnknown.fromString(json.optString("disabled", null));

        // fullMailbox
        if (!json.has("fullMailbox")) {
            throw new ApiClient.ApiResponseUnusableException("Missing required field 'account.fullMailbox' in response.");
        }
        YesNoUnknown fullMailbox = YesNoUnknown.fromString(json.optString("fullMailbox", null));

        // role
        if (!json.has("role")) {
            throw new ApiClient.ApiResponseUnusableException("Missing required field 'account.role' in response.");
        }
        YesNoUnknown role = YesNoUnknown.fromString(json.optString("role", null));

        return new Account(disabled, fullMailbox, role);
    }
}
