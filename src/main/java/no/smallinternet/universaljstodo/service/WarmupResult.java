package no.smallinternet.universaljstodo.service;

public final class WarmupResult {
    private final long[] stats;

    public WarmupResult(long[] stats) {
        this.stats = stats;
    }

    public long[] getStats() {
        return stats;
    }
}
