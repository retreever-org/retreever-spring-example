package dev.retreever.example.controller;

import dev.retreever.example.dto.envelope.ApiAck;
import dev.retreever.example.dto.envelope.ApiResponse;
import dev.retreever.example.dto.request.AuthResponse;
import dev.retreever.example.dto.request.LogoutRequest;
import dev.retreever.example.dto.request.RefreshRequest;
import dev.retreever.example.dto.request.UserCredentials;
import dev.retreever.example.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/public/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AuthResponse>> loginUser(
            @RequestHeader(name = "X-Device-ID", required = false) String deviceId,
            @Valid @ModelAttribute UserCredentials userCredentials
    ) {
        AuthResponse response = authService.loginUser(userCredentials, deviceId);
        return ResponseEntity
                .ok()
                .body(ApiResponse.success(
                        "login successful",
                        response
                ));
    }

    @PostMapping(value = "/public/login/refresh", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AuthResponse>> refreshLogin(
            @RequestHeader(name = "X-Device-ID", required = false) String deviceId,
            @Valid @RequestBody RefreshRequest request
    ) {
        AuthResponse response = authService.refreshLogin(request.refreshToken(), deviceId);
        return ResponseEntity
                .ok()
                .body(ApiResponse.success(
                        "login successful",
                        response
                ));
    }

    @PostMapping(value = "/public/logout", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiAck> logoutUser(
            @RequestHeader(name = "X-Device-ID", required = false) String deviceId,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,
            @Valid @RequestBody LogoutRequest request
    ) {
        String accessToken = request.accessToken() != null ? request.accessToken() : authorizationHeader;
        authService.logoutUser(request.refreshToken(), accessToken);
        return ResponseEntity.ok(ApiAck.success(
                "Logout Successful.",
                deviceId == null || deviceId.isBlank() ? authorizationHeader : deviceId
        ));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/users/roles")
    public ResponseEntity<ApiResponse<List<String>>> getUserRoles(Authentication authentication) {
        var userRoles = authService.getUserRoles(authentication);
        return ResponseEntity
                .ok(ApiResponse.success(
                        "Roles Fetched",
                        userRoles
                ));
    }
}
