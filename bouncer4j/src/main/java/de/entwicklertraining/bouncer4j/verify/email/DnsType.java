package de.entwicklertraining.bouncer4j.verify.email;

/**
 * Gibt den Typ des DNS-Eintrags an.
 *
 * Mögliche Werte:
 *  - MX
 *  - A
 */
public enum DnsType {
    MX,
    A,
    UNKNOWN
}
