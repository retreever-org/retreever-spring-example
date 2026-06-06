package dev.retreever.example.security;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class ApiRateLimiter {

    private static final String REQUEST_ACTION = "api";
    private static final String UPLOAD_ACTION = "upload";

    private final InMemoryRateLimitRepository rateLimitRepository;
    private final InMemoryDeviceAddressRepository deviceAddressRepository;
    private final ApiRateLimitProperties properties;
    private final Clock clock;
    private final ZoneId zoneId;

    public ApiRateLimiter(
            InMemoryRateLimitRepository rateLimitRepository,
            InMemoryDeviceAddressRepository deviceAddressRepository,
            ApiRateLimitProperties properties
    ) {
        this.rateLimitRepository = rateLimitRepository;
        this.deviceAddressRepository = deviceAddressRepository;
        this.properties = properties;
        this.clock = Clock.systemUTC();
        this.zoneId = ZoneId.systemDefault();
    }

    public RateLimitDecision validateRequest(DeviceRequestIdentity identity) {
        return validate(identity, REQUEST_ACTION, 1, properties.getRequest());
    }

    public RateLimitDecision validateUpload(DeviceRequestIdentity identity, int uploadUnits) {
        return validate(identity, UPLOAD_ACTION, uploadUnits, properties.getUpload());
    }

    private RateLimitDecision validate(
            DeviceRequestIdentity identity,
            String action,
            int amount,
            ApiRateLimitProperties.Limit limits
    ) {
        Instant now = clock.instant();
        deviceAddressRepository.track(identity.deviceId(), identity.clientIp(), now);

        RateLimitDecision minuteDecision = applyWindow(identity, action, "minute", amount, limits.getPerMinute(), now);
        if (!minuteDecision.allowed()) {
            return minuteDecision;
        }

        return applyWindow(identity, action, "day", amount, limits.getPerDay(), now);
    }

    private RateLimitDecision applyWindow(
            DeviceRequestIdentity identity,
            String action,
            String window,
            int amount,
            int limit,
            Instant now
    ) {
        BucketWindow bucketWindow = resolveBucketWindow(now, window);
        String key = action + ":" + window + ":" + identity.subjectKey() + ":" + bucketWindow.bucketKey();
        int current = rateLimitRepository.increment(key, bucketWindow.expiresAt(), amount, now);
        if (current > limit) {
            return RateLimitDecision.rejected(limit, current, window, action);
        }
        return RateLimitDecision.allowed(limit, current, window, action);
    }

    private BucketWindow resolveBucketWindow(Instant now, String window) {
        ZonedDateTime zonedDateTime = now.atZone(zoneId);
        if ("minute".equals(window)) {
            ZonedDateTime nextMinute = zonedDateTime.plusMinutes(1).withSecond(0).withNano(0);
            String bucketKey = zonedDateTime.getYear()
                    + "-" + zonedDateTime.getDayOfYear()
                    + "-" + zonedDateTime.getHour()
                    + "-" + zonedDateTime.getMinute();
            return new BucketWindow(bucketKey, nextMinute.toInstant());
        }

        LocalDate localDate = zonedDateTime.toLocalDate();
        ZonedDateTime nextDay = localDate.plusDays(1).atStartOfDay(zoneId);
        return new BucketWindow(localDate.toString(), nextDay.toInstant());
    }

    private record BucketWindow(
            String bucketKey,
            Instant expiresAt
    ) {
    }
}
