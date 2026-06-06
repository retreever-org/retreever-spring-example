package dev.retreever.example.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class MockBearerAuthenticationFilter extends OncePerRequestFilter {

    private static final List<String> EXCLUDED_PATH_PREFIXES = List.of(
            "/api/v1/public/",
            "/api/v1/scenarios/public/"
    );

    private final MockIdentityService identityService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final MockDeviceCookieService deviceCookieService;

    public MockBearerAuthenticationFilter(
            MockIdentityService identityService,
            AuthenticationEntryPoint authenticationEntryPoint,
            MockDeviceCookieService deviceCookieService
    ) {
        this.identityService = identityService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.deviceCookieService = deviceCookieService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        if (!requestUri.startsWith("/api/v1")) {
            return true;
        }
        return EXCLUDED_PATH_PREFIXES.stream().anyMatch(requestUri::startsWith);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("New Request: {}", request.getRequestURI());
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        String deviceId = deviceCookieService.getDeviceId(request);
        if (deviceId == null || deviceId.isBlank()) {
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new InsufficientAuthenticationException("Device cookie is required for authenticated requests.")
            );
            return;
        }

        try {
            MockAuthenticatedUser user = identityService.authenticate(authorizationHeader, deviceId);
            deviceCookieService.refreshDeviceCookie(request, response, deviceId);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    authorizationHeader,
                    user.grantedAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, ex);
        }
    }
}
