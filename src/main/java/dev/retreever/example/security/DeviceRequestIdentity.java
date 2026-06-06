package dev.retreever.example.security;

public record DeviceRequestIdentity(
        String deviceId,
        String clientIp
) {

    public String subjectKey() {
        return deviceId + "@" + clientIp;
    }
}
