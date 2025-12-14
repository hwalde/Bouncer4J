package de.entwicklertraining.bouncer4j.verify.email;

/**
 * Gibt den Verifizierungsstatus der E-Mail-Adresse an.
 *
 * Mögliche Werte:
 *  - DELIVERABLE
 *  - RISKY
 *  - UNDELIVERABLE
 *  - UNKNOWN
 */
public enum EmailStatus {
    DELIVERABLE,
    RISKY,
    UNDELIVERABLE,
    UNKNOWN
}
