package de.entwicklertraining.bouncer4j.verify.email;

/**
 * Vereinheitlicht die Werte:
 *  - yes
 *  - no
 *  - unknown
 */
public enum YesNoUnknown {
    YES, NO, UNKNOWN;

    public static YesNoUnknown fromString(String raw) {
        if (raw == null) {
            return UNKNOWN;
        }
        switch (raw.toLowerCase()) {
            case "yes":
                return YES;
            case "no":
                return NO;
            default:
                return UNKNOWN;
        }
    }
}
