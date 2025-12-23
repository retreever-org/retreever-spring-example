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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/public/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AuthResponse>> loginUser(@Valid UserCredentials userCredentials) {
        AuthResponse response = authService.loginUser(userCredentials);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "login successful",
                        response
                ));
    }

    @PostMapping(value = "/public/login/refresh", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AuthResponse>> refreshLogin(RefreshRequest request) {
        AuthResponse response = authService.refreshLogin(request.refreshToken());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "login successful",
                        response
                ));
    }

    @PostMapping(value = "/public/logout", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiAck> logoutUser(LogoutRequest request) {
        authService.logoutUser(request.refreshToken(), request.accessToken());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiAck.success("Logout Successful."));
    }

    @GetMapping("/users/roles")
    public ResponseEntity<ApiResponse<List<String>>> getUserRoles() {
        var userRoles = authService.getUserRoles();
        return ResponseEntity
                .ok(ApiResponse.success(
                        "Roles Fetched",
                        userRoles
                ));
    }
}
