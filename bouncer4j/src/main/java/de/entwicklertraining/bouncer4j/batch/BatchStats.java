package de.entwicklertraining.bouncer4j.batch;

import org.json.JSONObject;

/**
 * Hilfsklasse zum Speichern von Stats-Feldern:
 * {
 *   "deliverable": 1,
 *   "risky": 0,
 *   "undeliverable": 1,
 *   "unknown": 0
 * }
 */
public final class BatchStats {

    private final int deliverable;
    private final int risky;
    private final int undeliverable;
    private final int unknown;

    private BatchStats(int deliverable, int risky, int undeliverable, int unknown) {
        this.deliverable = deliverable;
        this.risky = risky;
        this.undeliverable = undeliverable;
        this.unknown = unknown;
    }

    public static BatchStats fromJson(JSONObject obj) {
        int d = obj.optInt("deliverable", 0);
        int r = obj.optInt("risky", 0);
        int u = obj.optInt("undeliverable", 0);
        int unk = obj.optInt("unknown", 0);
        return new BatchStats(d, r, u, unk);
    }

    public int getDeliverable() {
        return deliverable;
    }

    public int getRisky() {
        return risky;
    }

    public int getUndeliverable() {
        return undeliverable;
    }

    public int getUnknown() {
        return unknown;
    }
}
