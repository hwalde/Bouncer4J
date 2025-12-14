package de.entwicklertraining.bouncer4j;

/**
 * Data Transfer Object (DTO) für das Ergebnis der E-Mail-Prüfung.
 *
 * @param email            Die geprüfte E-Mail-Adresse.
 * @param isReachable      Gibt an, ob die E-Mail-Adresse technisch wahrscheinlich erreichbar ist (Status deliverable oder risky).
 * @param isSafeToSend     Gibt an, ob der Versand an diese Adresse aus Reputationssicht empfohlen wird.
 * @param reasonIfNotSafe  Eine Begründung, warum der Versand nicht empfohlen wird (null, wenn isSafeToSend true ist).
 */
public record EmailCheckResult(
        String email,
        boolean isReachable,
        boolean isSafeToSend,
        String reasonIfNotSafe
) {}