package de.entwicklertraining.bouncer4j.exceptions;

/**
 * Basisklasse für alle Bouncer-bezogenen Runtime-Exceptions.
 */
public class BouncerException extends RuntimeException {
    public BouncerException(String message) {
        super(message);
    }

    public BouncerException(String message, Throwable cause) {
        super(message, cause);
    }
}
