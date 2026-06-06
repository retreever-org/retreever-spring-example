package dev.retreever.example.security;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryDeviceAddressRepository {

    private final Map<String, Map<String, Instant>> addressesByDevice = new ConcurrentHashMap<>();

    public void track(String deviceId, String clientIp, Instant seenAt) {
        addressesByDevice
                .computeIfAbsent(deviceId, ignored -> new ConcurrentHashMap<>())
                .put(clientIp, seenAt);
    }

    public Set<String> findKnownIps(String deviceId) {
        return Set.copyOf(addressesByDevice.getOrDefault(deviceId, Map.of()).keySet());
    }

    public void purgeOlderThan(Instant cutoff) {
        addressesByDevice.entrySet().removeIf(entry -> {
            entry.getValue().entrySet().removeIf(ipEntry -> ipEntry.getValue().isBefore(cutoff));
            return entry.getValue().isEmpty();
        });
    }
}
