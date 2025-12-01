package dev.retreever.example.controller;

import dev.retreever.example.dto.envelope.ApiAck;
import dev.retreever.example.dto.envelope.ApiResponse;
import dev.retreever.example.dto.request.SellerEditRequest;
import dev.retreever.example.dto.request.UpdateUserProfileRequest;
import dev.retreever.example.dto.request.UserCredentials;
import dev.retreever.example.dto.response.UserProfileResponse;
import dev.retreever.example.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * REST controller for user profile management.
 * Handles user registration, profile creation, updates, and retrieval.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * Health check endpoint for the user service.
     */
    @GetMapping("/public/health")
    public ResponseEntity<ApiAck> health() {
        return ResponseEntity.ok(ApiAck.success("User service is healthy"));
    }

    /**
     * Registers a new user in the system.
     * Public endpoint - no authentication required.
     */
    @PostMapping("/public/users/register")
    public ResponseEntity<ApiAck> registerUser(@Valid @RequestBody UserCredentials request) {
        log.debug("User registration request received for email: {}", request.email());
        userProfileService.registerNewUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/api/v1/users/profile"))
                .body(ApiAck.success("User registered successfully"));
    }

    /**
     * Updates the user profile for the authenticated user.
     * Requires authentication.
     */
    @PutMapping("/users/profile")
    @PreAuthorize("hasAuthority('customer')")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(@Valid @RequestBody UpdateUserProfileRequest request) {
        log.debug("Update profile request received");
        UserProfileResponse response = userProfileService.updateProfile(request);

        return ResponseEntity
                .ok(ApiResponse.success("Profile updated successfully", response));
    }

    /**
     * Retrieves the current user's profile.
     * Requires authentication.
     */
    @GetMapping("/users/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentProfile() {
        log.debug("Get current profile request received");

        UserProfileResponse response = userProfileService.getUserProfile();
        return ResponseEntity.ok(ApiResponse.success(
                "Profile fetched successfully",
                response
        ));
    }

    /**
     * Creates a seller profile for the authenticated user.
     * Requires authentication.
     *
     * @return response indicating success or failure
     */
    @PostMapping("/sellers/profile")
    @PreAuthorize("hasAuthority('customer')")
    public ResponseEntity<ApiResponse<UserProfileResponse>> createSellerProfile() {
        log.debug("Creating seller profile");

        UserProfileResponse response = userProfileService.createSellerProfile();
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/api/v1/users/profile"))
                .body(ApiResponse.success(
                        "Seller Profile Created Successfully",
                        response
                ));
    }

    /**
     * Updates the seller profile for the authenticated user.
     * Requires authentication.
     *
     * @param request request containing the seller's bio
     * @return response indicating success or failure
     */
    @PutMapping("/sellers/profile")
    @PreAuthorize("hasAuthority('seller')")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateSellerProfile(@RequestBody @Valid SellerEditRequest request) {
        log.debug("Updating seller profile");

        UserProfileResponse response = userProfileService.updateSellerProfile(request);
        return ResponseEntity
                .ok(ApiResponse.success(
                        "Seller Profile Updated Successfully",
                        response
                ));
    }

    @PostMapping("/admins/register")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<ApiAck> registerAdmin(@Valid @RequestBody UserCredentials credentials) {
        log.debug("Creating new Admin");

        userProfileService.registerAdmin(credentials);
        return ResponseEntity
                .created(URI.create("/api/v1/users/profile"))
                .body(ApiAck.success("Admin created successfully"));
    }
}