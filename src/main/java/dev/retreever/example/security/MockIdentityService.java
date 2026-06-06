package dev.retreever.example.security;

import dev.retreever.example.dto.request.AuthResponse;
import dev.retreever.example.dto.request.UserCredentials;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MockIdentityService {

    private static final Duration ACCESS_TTL = Duration.ofMinutes(15);
    private static final Duration REFRESH_TTL = Duration.ofDays(7);
    public static final String DEFAULT_DEMO_EMAIL = "admin@quickcart.test";
    public static final String DEFAULT_DEMO_PASSWORD = "Passw0rd!";
    private static final String DEFAULT_DEMO_LOGIN_HINT =
            "You are not logged in. You can use dummy login: "
                    + DEFAULT_DEMO_EMAIL
                    + " / "
                    + DEFAULT_DEMO_PASSWORD;

    private final PasswordEncoder passwordEncoder;
    private final Map<String, MockAccount> accounts = new ConcurrentHashMap<>();
    private final Map<String, MockToken> accessTokens = new ConcurrentHashMap<>();
    private final Map<String, MockToken> refreshTokens = new ConcurrentHashMap<>();
    private final Map<String, Instant> revokedTokens = new ConcurrentHashMap<>();
    private final Map<String, Instant> deviceLastSeen = new ConcurrentHashMap<>();
    private final Set<String> seededAccounts = ConcurrentHashMap.newKeySet();

    public MockIdentityService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        seedAccounts();
    }

    public AuthResponse login(UserCredentials credentials, String deviceId) {
        if (credentials == null) {
            throw new BadCredentialsException("Credentials are required.");
        }

        MockAccount account = loadAccountByEmail(credentials.email());
        validateAccount(account);

        if (!passwordEncoder.matches(credentials.password(), account.passwordHash())) {
            throw new BadCredentialsException("Invalid email or password.");
        }

        String normalizedDeviceId = requireDeviceId(deviceId);
        touchAccount(account.email());
        touchDevice(normalizedDeviceId);
        return issueTokens(account, normalizedDeviceId);
    }

    public AuthResponse refresh(String refreshToken, String deviceId) {
        MockToken token = requireValidToken(cleanToken(refreshToken), TokenKind.REFRESH, refreshTokens);
        String normalizedDeviceId = requireDeviceId(deviceId);

        if (!Objects.equals(token.deviceId(), normalizedDeviceId)) {
            throw new BadCredentialsException("Refresh token is bound to a different device.");
        }

        touchDevice(normalizedDeviceId);
        touchAccount(token.email());
        revokeToken(token.value());
        return issueTokens(loadAccountByEmail(token.email()), normalizedDeviceId);
    }

    public void logout(String refreshToken, String accessToken) {
        revokeToken(cleanToken(refreshToken));
        revokeToken(cleanToken(accessToken));
    }

    public MockAuthenticatedUser authenticate(String authorizationHeader, String deviceId) {
        String rawToken = extractBearerToken(authorizationHeader);
        String normalizedDeviceId = requireDeviceId(deviceId);

        MockToken token = requireValidToken(rawToken, TokenKind.ACCESS, accessTokens);
        if (!Objects.equals(token.deviceId(), normalizedDeviceId)) {
            throw new BadCredentialsException("Access token is bound to a different device.");
        }

        touchDevice(normalizedDeviceId);
        return new MockAuthenticatedUser(token.email(), token.deviceId(), token.authorities());
    }

    public MockAuthenticatedUser currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof MockAuthenticatedUser principal)) {
            throw new InsufficientAuthenticationException("Authentication is required.");
        }
        return principal;
    }

    public List<String> currentAuthorities(Authentication authentication) {
        return currentUser(authentication).authorities().stream().sorted().toList();
    }

    public MockAccount loadAccountByEmail(String email) {
        MockAccount account = accounts.get(normalizeEmail(email));
        if (account == null) {
            throw new BadCredentialsException("Unknown account.");
        }
        return account;
    }

    public MockAccount registerCustomer(String email, String password) {
        return register(email, password, Set.of("customer"));
    }

    public MockAccount registerAdmin(String email, String password) {
        return register(email, password, Set.of("customer", "seller", "admin"));
    }

    public MockAccount promoteCurrentUserToSeller(Authentication authentication) {
        MockAuthenticatedUser currentUser = currentUser(authentication);
        MockAccount account = loadAccountByEmail(currentUser.email());

        if (account.authorities().contains("seller")) {
            return account;
        }

        MockAccount promoted = account.withAuthorities(Set.of("customer", "seller"), Instant.now());
        accounts.put(promoted.email(), promoted);
        return promoted;
    }

    public MockAccount currentAccount(Authentication authentication) {
        return loadAccountByEmail(currentUser(authentication).email());
    }

    public static String defaultDemoLoginHint() {
        return DEFAULT_DEMO_LOGIN_HINT;
    }

    private MockAccount register(String email, String password, Set<String> authorities) {
        String normalizedEmail = normalizeEmail(email);
        if (accounts.containsKey(normalizedEmail)) {
            throw new IllegalArgumentException("Account already exists for " + normalizedEmail);
        }

        MockAccount account = new MockAccount(
                normalizedEmail,
                passwordEncoder.encode(password),
                Set.copyOf(authorities),
                true,
                false,
                Instant.now(),
                Instant.now()
        );
        accounts.put(normalizedEmail, account);
        return account;
    }

    private void validateAccount(MockAccount account) {
        if (!account.enabled()) {
            throw new DisabledException("This account is disabled.");
        }
        if (account.locked()) {
            throw new LockedException("This account is locked.");
        }
    }

    private AuthResponse issueTokens(MockAccount account, String deviceId) {
        String accessToken = generateToken(account.email(), TokenKind.ACCESS);
        String refreshToken = generateToken(account.email(), TokenKind.REFRESH);

        Instant now = Instant.now();
        accessTokens.put(accessToken, new MockToken(
                accessToken,
                TokenKind.ACCESS,
                account.email(),
                deviceId,
                account.authorities(),
                now.plus(ACCESS_TTL),
                now
        ));
        refreshTokens.put(refreshToken, new MockToken(
                refreshToken,
                TokenKind.REFRESH,
                account.email(),
                deviceId,
                account.authorities(),
                now.plus(REFRESH_TTL),
                now
        ));

        return new AuthResponse(
                accessToken,
                (int) ACCESS_TTL.toSeconds(),
                refreshToken,
                (int) REFRESH_TTL.toSeconds(),
                "Bearer"
        );
    }

    private MockToken requireValidToken(String tokenValue, TokenKind tokenKind, Map<String, MockToken> registry) {
        pruneExpiredTokens(registry);

        if (tokenValue == null || tokenValue.isBlank()) {
            throw new InsufficientAuthenticationException(tokenKind == TokenKind.ACCESS
                    ? "Bearer token is required."
                    : "Refresh token is required.");
        }
        if (revokedTokens.containsKey(tokenValue)) {
            throw new CredentialsExpiredException("Token has been revoked.");
        }

        MockToken token = registry.get(tokenValue);
        if (token == null || token.kind() != tokenKind) {
            throw new BadCredentialsException("Token is invalid.");
        }
        if (token.expiresAt().isBefore(Instant.now())) {
            registry.remove(tokenValue);
            revokedTokens.put(tokenValue, Instant.now());
            throw new CredentialsExpiredException("Token has expired.");
        }

        Instant now = Instant.now();
        registry.put(tokenValue, token.touch(now));
        MockAccount account = loadAccountByEmail(token.email());
        validateAccount(account);
        touchAccount(account.email());
        return registry.get(tokenValue);
    }

    private void revokeToken(String tokenValue) {
        if (tokenValue == null || tokenValue.isBlank()) {
            return;
        }
        accessTokens.remove(tokenValue);
        refreshTokens.remove(tokenValue);
        revokedTokens.put(tokenValue, Instant.now());
    }

    private void pruneExpiredTokens(Map<String, MockToken> registry) {
        Instant now = Instant.now();
        registry.values().removeIf(token -> {
            boolean expired = token.expiresAt().isBefore(now);
            if (expired) {
                revokedTokens.put(token.value(), now);
            }
            return expired;
        });
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new InsufficientAuthenticationException("Bearer token is required.");
        }
        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Authorization header must use the Bearer scheme.");
        }
        return cleanToken(authorizationHeader.substring("Bearer ".length()));
    }

    private String cleanToken(String tokenValue) {
        return tokenValue == null ? null : tokenValue.trim();
    }

    private String requireDeviceId(String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            throw new InsufficientAuthenticationException("Device cookie is required.");
        }
        return deviceId.trim();
    }

    private void touchDevice(String deviceId) {
        deviceLastSeen.put(deviceId, Instant.now());
    }

    public void purgeOlderThan(Instant cutoff) {
        pruneExpiredTokens(accessTokens);
        pruneExpiredTokens(refreshTokens);

        Set<String> staleDevices = deviceLastSeen.entrySet().stream()
                .filter(entry -> entry.getValue().isBefore(cutoff))
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toSet());

        if (!staleDevices.isEmpty()) {
            accessTokens.values().removeIf(token -> staleDevices.contains(token.deviceId()) || token.lastTouchedAt().isBefore(cutoff));
            refreshTokens.values().removeIf(token -> staleDevices.contains(token.deviceId()) || token.lastTouchedAt().isBefore(cutoff));
            staleDevices.forEach(deviceLastSeen::remove);
        } else {
            accessTokens.values().removeIf(token -> token.lastTouchedAt().isBefore(cutoff));
            refreshTokens.values().removeIf(token -> token.lastTouchedAt().isBefore(cutoff));
        }

        revokedTokens.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));
        accounts.entrySet().removeIf(entry -> shouldRemoveAccount(entry.getValue(), cutoff));
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private String generateToken(String subject, TokenKind tokenKind) {
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        String header = encoder.encodeToString("retreever".getBytes(StandardCharsets.UTF_8));
        String payload = encoder.encodeToString((subject + ":" + tokenKind.name() + ":" + Instant.now()).getBytes(StandardCharsets.UTF_8));
        String signature = encoder.encodeToString(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        return header + "." + payload + "." + signature;
    }

    private void seedAccounts() {
        seed("customer@quickcart.test", "Passw0rd!", Set.of("customer"), true, false);
        seed("seller@quickcart.test", "Passw0rd!", Set.of("customer", "seller"), true, false);
        seed(DEFAULT_DEMO_EMAIL, DEFAULT_DEMO_PASSWORD, Set.of("customer", "seller", "admin"), true, false);
        seed("locked@quickcart.test", "Passw0rd!", Set.of("customer"), true, true);
        seed("disabled@quickcart.test", "Passw0rd!", Set.of("customer"), false, false);
    }

    private void seed(String email, String password, Set<String> authorities, boolean enabled, boolean locked) {
        String normalizedEmail = normalizeEmail(email);
        accounts.put(normalizedEmail, new MockAccount(
                normalizedEmail,
                passwordEncoder.encode(password),
                Set.copyOf(authorities),
                enabled,
                locked,
                Instant.now(),
                Instant.now()
        ));
        seededAccounts.add(normalizedEmail);
    }

    private void touchAccount(String email) {
        String normalizedEmail = normalizeEmail(email);
        accounts.computeIfPresent(normalizedEmail, (ignored, account) -> account.touch(Instant.now()));
    }

    private boolean shouldRemoveAccount(MockAccount account, Instant cutoff) {
        if (seededAccounts.contains(account.email())) {
            return false;
        }
        return account.lastTouchedAt().isBefore(cutoff);
    }

    public record MockAccount(
            String email,
            String passwordHash,
            Set<String> authorities,
            boolean enabled,
            boolean locked,
            Instant createdAt,
            Instant lastTouchedAt
    ) {
        public MockAccount {
            authorities = authorities == null ? Set.of() : Set.copyOf(authorities);
        }

        public MockAccount withAuthorities(Set<String> newAuthorities, Instant touchedAt) {
            return new MockAccount(email, passwordHash, Set.copyOf(newAuthorities), enabled, locked, createdAt, touchedAt);
        }

        public MockAccount touch(Instant touchedAt) {
            return new MockAccount(email, passwordHash, authorities, enabled, locked, createdAt, touchedAt);
        }
    }

    private record MockToken(
            String value,
            TokenKind kind,
            String email,
            String deviceId,
            Set<String> authorities,
            Instant expiresAt,
            Instant lastTouchedAt
    ) {
        private MockToken touch(Instant touchedAt) {
            return new MockToken(value, kind, email, deviceId, authorities, expiresAt, touchedAt);
        }
    }

    private enum TokenKind {
        ACCESS,
        REFRESH
    }
}
