package de.entwicklertraining.bouncer4j;

/**
 * Konfiguration für den BouncerCheckService, um die Kriterien für "isSafeToSend" anzupassen.
 */
public class BouncerCheckConfig {

    /**
     * Maximale erlaubte Bouncer-Toxizitätsstufe (0-5). Bouncer empfiehlt, Mails mit 4 oder 5 zu blockieren,
     * da sie die Reputation der Sende-Infrastruktur beeinträchtigen können.
     * Standard: 4 (d.h., 0-3 sind erlaubt).
     */
    private int maxAllowedToxicity = 3; // Entspricht Blockieren von 4 und 5

    /**
     * Blockiert den Versand an Adressen, deren Domain als "accept all" / "catch all" konfiguriert ist (Status: risky).
     * Diese Adressen können Spam-Traps sein oder niedrige Engagement-Raten haben.
     * Standard: true (Blockieren).
     */
    private boolean blockAcceptAll = true;

    /**
     * Blockiert den Versand an Wegwerf-/Temporär-E-Mail-Adressen (Status: risky).
     * Diese deuten auf geringes Interesse oder Missbrauchsabsicht hin.
     * Standard: true (Blockieren).
     */
    private boolean blockDisposable = true;

    /**
     * Blockiert den Versand an volle Postfächer (Status: risky).
     * Führt zu (temporären) Soft Bounces, was bei wiederholtem Senden die Reputation schädigen kann.
     * Standard: true (Blockieren).
     */
    private boolean blockFullMailbox = true;

    /**
     * Behandelt E-Mails mit dem Status "unknown" generell als unsicher für den Versand.
     * Gründe können DNS-Probleme, Timeout, nicht erreichbare Server etc. sein. In Bulk-Szenarien ist es sicherer, diese zu meiden.
     * Standard: true (Behandeln als unsicher).
     */
    private boolean treatUnknownAsUnsafe = true;

    // --- Standard-Konstruktor ---
    public BouncerCheckConfig() {}

    // --- Getter und Setter (oder Builder Pattern) ---

    public int getMaxAllowedToxicity() {
        return maxAllowedToxicity;
    }

    public BouncerCheckConfig setMaxAllowedToxicity(int maxAllowedToxicity) {
        if (maxAllowedToxicity < 0 || maxAllowedToxicity > 5) {
            throw new IllegalArgumentException("maxAllowedToxicity must be between 0 and 5");
        }
        this.maxAllowedToxicity = maxAllowedToxicity;
        return this;
    }

    public boolean isBlockAcceptAll() {
        return blockAcceptAll;
    }

    public BouncerCheckConfig setBlockAcceptAll(boolean blockAcceptAll) {
        this.blockAcceptAll = blockAcceptAll;
        return this;
    }

    public boolean isBlockDisposable() {
        return blockDisposable;
    }

    public BouncerCheckConfig setBlockDisposable(boolean blockDisposable) {
        this.blockDisposable = blockDisposable;
        return this;
    }

    public boolean isBlockFullMailbox() {
        return blockFullMailbox;
    }

    public BouncerCheckConfig setBlockFullMailbox(boolean blockFullMailbox) {
        this.blockFullMailbox = blockFullMailbox;
        return this;
    }

    public boolean isTreatUnknownAsUnsafe() {
        return treatUnknownAsUnsafe;
    }

    public BouncerCheckConfig setTreatUnknownAsUnsafe(boolean treatUnknownAsUnsafe) {
        this.treatUnknownAsUnsafe = treatUnknownAsUnsafe;
        return this;
    }

    /**
     * Erstellt eine Standardkonfiguration.
     * @return Eine neue Instanz mit Standardwerten.
     */
    public static BouncerCheckConfig standard() {
        return new BouncerCheckConfig();
    }
}