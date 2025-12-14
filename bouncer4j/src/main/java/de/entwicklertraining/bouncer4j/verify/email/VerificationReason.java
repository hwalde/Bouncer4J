package de.entwicklertraining.bouncer4j.verify.email;

/**
 * Erläutert den spezifischen Grund für den Verifizierungsstatus.
 *
 * Mögliche Werte:
 *  - ACCEPTED_EMAIL
 *  - LOW_DELIVERABILITY
 *  - LOW_QUALITY
 *  - INVALID_EMAIL
 *  - INVALID_DOMAIN
 *  - REJECTED_EMAIL
 *  - DNS_ERROR
 *  - UNAVAILABLE_SMTP
 *  - UNSUPPORTED
 *  - TIMEOUT
 *  - UNKNOWN
 */
public enum VerificationReason {
    ACCEPTED_EMAIL,
    LOW_DELIVERABILITY,
    LOW_QUALITY,
    INVALID_EMAIL,
    INVALID_DOMAIN,
    REJECTED_EMAIL,
    DNS_ERROR,
    UNAVAILABLE_SMTP,
    UNSUPPORTED,
    TIMEOUT,
    UNKNOWN
}
