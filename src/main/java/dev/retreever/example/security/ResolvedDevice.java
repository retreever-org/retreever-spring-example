package dev.retreever.example.security;

public record ResolvedDevice(
        String deviceId,
        String clientIp
) {
}
