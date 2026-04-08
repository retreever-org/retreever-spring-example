package dev.retreever.example.service;

import dev.retreever.example.dto.request.AuthResponse;
import dev.retreever.example.dto.request.UserCredentials;
import dev.retreever.example.security.MockIdentityService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    private final MockIdentityService identityService;

    public AuthService(MockIdentityService identityService) {
        this.identityService = identityService;
    }

    public AuthResponse loginUser(@Valid UserCredentials userCredentials, String deviceId) {
        return identityService.login(userCredentials, deviceId);
    }

    public AuthResponse refreshLogin(String refreshToken, String deviceId) {
        return identityService.refresh(refreshToken, deviceId);
    }

    public void logoutUser(String refreshToken, String accessToken) {
        identityService.logout(refreshToken, accessToken);
    }

    public List<String> getUserRoles(Authentication authentication) {
        return identityService.currentAuthorities(authentication);
    }
}
