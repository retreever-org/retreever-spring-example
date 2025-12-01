package dev.retreever.example.service;

import dev.retreever.example.dto.request.AuthResponse;
import dev.retreever.example.dto.request.UserCredentials;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    public AuthResponse loginUser(@Valid UserCredentials userCredentials) {
        return null;
    }

    public AuthResponse refreshLogin(String refreshToken) {
        return null;
    }

    public void logoutUser(String refreshToken, String accessToken) {

    }

    public List<String> getUserRoles() {
        return null;
    }
}
