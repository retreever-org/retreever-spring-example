package dev.retreever.example.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InMemoryStateCleanupScheduler {

    private final InMemoryStateCleanupService cleanupService;

    public InMemoryStateCleanupScheduler(InMemoryStateCleanupService cleanupService) {
        this.cleanupService = cleanupService;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    public void cleanupAtMidnightUtc() {
        cleanupService.purgeStaleState();
    }
}
