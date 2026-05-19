package dev.retreever.example.service;

import dev.retreever.example.dto.request.AuthResponse;
import dev.retreever.example.dto.request.UserCredentials;
import dev.retreever.example.security.MockDeviceCookieService;
import dev.retreever.example.security.MockIdentityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    private final MockIdentityService identityService;
    private final MockDeviceCookieService deviceCookieService;

    public AuthService(MockIdentityService identityService, MockDeviceCookieService deviceCookieService) {
        this.identityService = identityService;
        this.deviceCookieService = deviceCookieService;
    }

    public AuthResponse loginUser(
            @Valid UserCredentials userCredentials,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String deviceId = deviceCookieService.getOrCreateDeviceId(request, response);
        return identityService.login(userCredentials, deviceId);
    }

    public AuthResponse refreshLogin(String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        String deviceId = deviceCookieService.getDeviceId(request);
        deviceCookieService.refreshDeviceCookie(request, response, deviceId);
        return identityService.refresh(refreshToken, deviceId);
    }

    public void logoutUser(String refreshToken, String accessToken) {
        identityService.logout(refreshToken, accessToken);
    }

    public List<String> getUserRoles(Authentication authentication) {
        return identityService.currentAuthorities(authentication);
    }
}
