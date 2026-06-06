package dev.retreever.example.security;

import dev.retreever.example.dto.request.AuthResponse;
import dev.retreever.example.dto.request.UserCredentials;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class InMemoryStateCleanupIntegrationTest {

    @Autowired
    private MockIdentityService identityService;

    @Autowired
    private InMemoryDeviceAddressRepository deviceAddressRepository;

    @Autowired
    private InMemoryRateLimitRepository rateLimitRepository;

    @Test
    void purgeOlderThanRemovesStaleDynamicStateButKeepsSeededAccounts() {
        identityService.registerCustomer("cleanup-user@quickcart.test", "Passw0rd!");
        AuthResponse authResponse = identityService.login(
                new UserCredentials("cleanup-user@quickcart.test", "Passw0rd!"),
                "device-cleanup-1"
        );
        deviceAddressRepository.track("device-cleanup-1", "127.0.0.1", Instant.now());
        rateLimitRepository.increment("request:minute:device-cleanup-1@127.0.0.1:test", Instant.now().plusSeconds(60), 1, Instant.now());

        identityService.purgeOlderThan(Instant.now().plusSeconds(1));
        deviceAddressRepository.purgeOlderThan(Instant.now().plusSeconds(1));
        rateLimitRepository.purgeOlderThan(Instant.now().plusSeconds(1));

        assertDoesNotThrow(() -> identityService.loadAccountByEmail(MockIdentityService.DEFAULT_DEMO_EMAIL));
        assertThrows(BadCredentialsException.class, () -> identityService.loadAccountByEmail("cleanup-user@quickcart.test"));
        assertThrows(
                BadCredentialsException.class,
                () -> identityService.authenticate("Bearer " + authResponse.accessToken(), "device-cleanup-1")
        );
    }
}
