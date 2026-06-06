package dev.retreever.example.security;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Service
public class InMemoryStateCleanupService {

    private final MockIdentityService mockIdentityService;
    private final InMemoryDeviceAddressRepository deviceAddressRepository;
    private final InMemoryRateLimitRepository rateLimitRepository;
    private final InMemoryStateRetentionProperties retentionProperties;
    private final Clock clock;

    public InMemoryStateCleanupService(
            MockIdentityService mockIdentityService,
            InMemoryDeviceAddressRepository deviceAddressRepository,
            InMemoryRateLimitRepository rateLimitRepository,
            InMemoryStateRetentionProperties retentionProperties
    ) {
        this.mockIdentityService = mockIdentityService;
        this.deviceAddressRepository = deviceAddressRepository;
        this.rateLimitRepository = rateLimitRepository;
        this.retentionProperties = retentionProperties;
        this.clock = Clock.systemUTC();
    }

    public void purgeStaleState() {
        Instant cutoff = clock.instant().minus(retentionProperties.getMaxAge());
        mockIdentityService.purgeOlderThan(cutoff);
        deviceAddressRepository.purgeOlderThan(cutoff);
        rateLimitRepository.purgeOlderThan(cutoff);
    }
}
