package dev.retreever.example.service;

import dev.retreever.example.dto.request.SellerEditRequest;
import dev.retreever.example.dto.request.UpdateUserProfileRequest;
import dev.retreever.example.dto.request.UserCredentials;
import dev.retreever.example.dto.response.UserProfileResponse;
import dev.retreever.example.security.MockIdentityService;
import dev.retreever.example.service.support.MockDataFactory;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {
    private final MockIdentityService identityService;
    private final MockDataFactory mockDataFactory;

    public UserProfileService(MockIdentityService identityService, MockDataFactory mockDataFactory) {
        this.identityService = identityService;
        this.mockDataFactory = mockDataFactory;
    }

    public void registerNewUser(@Valid UserCredentials request) {
        identityService.registerCustomer(request.email(), request.password());
    }

    public UserProfileResponse updateProfile(Authentication authentication, @Valid UpdateUserProfileRequest request) {
        var account = identityService.currentAccount(authentication);
        return mockDataFactory.userProfile(account.email(), account.authorities());
    }

    public UserProfileResponse getUserProfile(Authentication authentication) {
        var account = identityService.currentAccount(authentication);
        return mockDataFactory.userProfile(account.email(), account.authorities());
    }

    public UserProfileResponse createSellerProfile(Authentication authentication) {
        var account = identityService.promoteCurrentUserToSeller(authentication);
        return mockDataFactory.userProfile(account.email(), account.authorities());
    }

    public UserProfileResponse updateSellerProfile(Authentication authentication, @Valid SellerEditRequest request) {
        var account = identityService.currentAccount(authentication);
        return mockDataFactory.userProfile(account.email(), account.authorities());
    }

    public void registerAdmin(@Valid UserCredentials credentials) {
        identityService.registerAdmin(credentials.email(), credentials.password());
    }
}
