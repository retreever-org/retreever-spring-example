package dev.retreever.example.security;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryRateLimitRepository {

    private final Map<String, CounterWindow> counters = new ConcurrentHashMap<>();
    private final AtomicLong writesSinceCleanup = new AtomicLong();

    public int increment(String key, Instant expiresAt, int amount, Instant now) {
        CounterWindow counterWindow = counters.compute(key, (ignored, current) -> {
            if (current == null || !current.expiresAt().isAfter(now)) {
                return new CounterWindow(amount, expiresAt, now);
            }
            current.increment(amount, now);
            return current;
        });

        cleanupExpiredEntries(now);
        return counterWindow.count();
    }

    public void purgeOlderThan(Instant cutoff) {
        counters.entrySet().removeIf(entry -> entry.getValue().lastUpdatedAt().isBefore(cutoff));
    }

    private void cleanupExpiredEntries(Instant now) {
        if (writesSinceCleanup.incrementAndGet() % 250 != 0) {
            return;
        }
        counters.entrySet().removeIf(entry -> !entry.getValue().expiresAt().isAfter(now));
    }

    private static final class CounterWindow {

        private final AtomicLong count;
        private final Instant expiresAt;
        private volatile Instant lastUpdatedAt;

        private CounterWindow(int initialValue, Instant expiresAt, Instant lastUpdatedAt) {
            this.count = new AtomicLong(initialValue);
            this.expiresAt = expiresAt;
            this.lastUpdatedAt = lastUpdatedAt;
        }

        private void increment(int amount, Instant now) {
            count.addAndGet(amount);
            lastUpdatedAt = now;
        }

        private int count() {
            return Math.toIntExact(count.get());
        }

        private Instant expiresAt() {
            return expiresAt;
        }

        private Instant lastUpdatedAt() {
            return lastUpdatedAt;
        }
    }
}
