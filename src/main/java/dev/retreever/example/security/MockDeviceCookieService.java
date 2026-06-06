package dev.retreever.example.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
public class MockDeviceCookieService {

    public static final String DEVICE_COOKIE_NAME = "did";
    public static final Duration DEVICE_IDLE_TTL = Duration.ofHours(12);
    private static final String RESOLVED_DEVICE_REQUEST_ATTRIBUTE = MockDeviceCookieService.class.getName() + ".resolvedDevice";

    private final ClientIpResolver clientIpResolver;
    private final SealedDeviceCookieCodec sealedDeviceCookieCodec;

    public MockDeviceCookieService(
            ClientIpResolver clientIpResolver,
            SealedDeviceCookieCodec sealedDeviceCookieCodec
    ) {
        this.clientIpResolver = clientIpResolver;
        this.sealedDeviceCookieCodec = sealedDeviceCookieCodec;
    }

    public String getDeviceId(HttpServletRequest request) {
        return resolveDevice(request).map(ResolvedDevice::deviceId).orElse(null);
    }

    public String getOrCreateDeviceId(HttpServletRequest request, HttpServletResponse response) {
        return resolveOrCreateDevice(request, response).deviceId();
    }

    public String getOrCreateDeviceId(HttpServletRequest request) {
        return resolveDevice(request)
                .map(ResolvedDevice::deviceId)
                .orElseGet(() -> UUID.randomUUID().toString());
    }

    public ResolvedDevice resolveOrCreateDevice(HttpServletRequest request, HttpServletResponse response) {
        String clientIp = clientIpResolver.resolve(request);
        Optional<ResolvedDevice> existing = resolveDevice(request);
        if (existing.isPresent()) {
            ResolvedDevice resolvedDevice = existing.get();
            if (!resolvedDevice.clientIp().equals(clientIp)) {
                ResolvedDevice reboundDevice = new ResolvedDevice(resolvedDevice.deviceId(), clientIp);
                writeDeviceCookie(request, response, reboundDevice);
                cacheResolvedDevice(request, reboundDevice);
                return reboundDevice;
            }

            refreshDeviceCookie(request, response, resolvedDevice.deviceId());
            cacheResolvedDevice(request, resolvedDevice);
            return resolvedDevice;
        }

        ResolvedDevice newDevice = new ResolvedDevice(UUID.randomUUID().toString(), clientIp);
        writeDeviceCookie(request, response, newDevice);
        cacheResolvedDevice(request, newDevice);
        return newDevice;
    }

    public void refreshDeviceCookie(HttpServletRequest request, HttpServletResponse response, String deviceId) {
        if (!hasText(deviceId)) {
            return;
        }
        writeDeviceCookie(request, response, new ResolvedDevice(deviceId.trim(), clientIpResolver.resolve(request)));
    }

    private Optional<ResolvedDevice> resolveDevice(HttpServletRequest request) {
        Object cachedDevice = request.getAttribute(RESOLVED_DEVICE_REQUEST_ATTRIBUTE);
        if (cachedDevice instanceof ResolvedDevice resolvedDevice) {
            return Optional.of(resolvedDevice);
        }

        String rawCookieValue = getRawCookieValue(request);
        if (!hasText(rawCookieValue)) {
            return Optional.empty();
        }
        Optional<ResolvedDevice> resolvedDevice = sealedDeviceCookieCodec.decode(rawCookieValue);
        resolvedDevice.ifPresent(device -> cacheResolvedDevice(request, device));
        return resolvedDevice;
    }

    private String getRawCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (DEVICE_COOKIE_NAME.equals(cookie.getName()) && hasText(cookie.getValue())) {
                return cookie.getValue().trim();
            }
        }

        return null;
    }

    private void writeDeviceCookie(HttpServletRequest request, HttpServletResponse response, ResolvedDevice resolvedDevice) {
        String sealedValue = sealedDeviceCookieCodec.encode(resolvedDevice);
        ResponseCookie cookie = ResponseCookie.from(DEVICE_COOKIE_NAME, sealedValue)
                .httpOnly(true)
                .secure(isSecureRequest(request))
                .sameSite("Lax")
                .path("/")
                .maxAge(DEVICE_IDLE_TTL)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void cacheResolvedDevice(HttpServletRequest request, ResolvedDevice resolvedDevice) {
        request.setAttribute(RESOLVED_DEVICE_REQUEST_ATTRIBUTE, resolvedDevice);
    }

    private boolean isSecureRequest(HttpServletRequest request) {
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        return request.isSecure() || "https".equalsIgnoreCase(forwardedProto);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
