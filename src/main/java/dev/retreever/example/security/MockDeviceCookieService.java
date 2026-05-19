package dev.retreever.example.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
public class MockDeviceCookieService {

    public static final String DEVICE_COOKIE_NAME = "did";
    public static final Duration DEVICE_IDLE_TTL = Duration.ofHours(12);

    public String getDeviceId(HttpServletRequest request) {
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

    public String getOrCreateDeviceId(HttpServletRequest request, HttpServletResponse response) {
        String deviceId = getDeviceId(request);
        if (!hasText(deviceId)) {
            deviceId = UUID.randomUUID().toString();
        }
        writeDeviceCookie(request, response, deviceId);
        return deviceId;
    }

    public void refreshDeviceCookie(HttpServletRequest request, HttpServletResponse response, String deviceId) {
        if (hasText(deviceId)) {
            writeDeviceCookie(request, response, deviceId.trim());
        }
    }

    private void writeDeviceCookie(HttpServletRequest request, HttpServletResponse response, String deviceId) {
        ResponseCookie cookie = ResponseCookie.from(DEVICE_COOKIE_NAME, deviceId)
                .httpOnly(true)
                .secure(isSecureRequest(request))
                .sameSite("Lax")
                .path("/")
                .maxAge(DEVICE_IDLE_TTL)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private boolean isSecureRequest(HttpServletRequest request) {
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        return request.isSecure() || "https".equalsIgnoreCase(forwardedProto);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
