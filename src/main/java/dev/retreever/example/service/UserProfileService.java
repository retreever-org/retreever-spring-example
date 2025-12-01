package dev.retreever.example.service;

import dev.retreever.example.dto.request.SellerEditRequest;
import dev.retreever.example.dto.request.UpdateUserProfileRequest;
import dev.retreever.example.dto.request.UserCredentials;
import dev.retreever.example.dto.response.UserProfileResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {
    public void registerNewUser(@Valid UserCredentials request) {
    }

    public UserProfileResponse updateProfile(@Valid UpdateUserProfileRequest request) {
        return null;
    }

    public UserProfileResponse getUserProfile() {
        return null;
    }

    public UserProfileResponse createSellerProfile() {
        return null;
    }

    public UserProfileResponse updateSellerProfile(@Valid SellerEditRequest request) {
        return null;
    }

    public void registerAdmin(@Valid UserCredentials credentials) {
    }
}
