package dev.retreever.example.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class DevCorsFilter extends OncePerRequestFilter {

    private static final String ALLOWED_METHODS = "GET,POST,PUT,PATCH,DELETE,OPTIONS";
    private static final String DEFAULT_ALLOWED_HEADERS = "Content-Type, Authorization";
    private static final String DEFAULT_MAX_AGE_SECONDS = "3600";

    private final List<String> allowedOrigins;

    public DevCorsFilter(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return allowedOrigins.isEmpty() || !StringUtils.hasText(request.getHeader(HttpHeaders.ORIGIN));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String origin = request.getHeader(HttpHeaders.ORIGIN);
        if (!allowedOrigins.contains(origin)) {
            if (isPreflightRequest(request)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } else {
                filterChain.doFilter(request, response);
            }
            return;
        }

        applyCorsHeaders(request, response, origin);

        if (isPreflightRequest(request)) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        filterChain.doFilter(request, response);
        applyCorsHeaders(request, response, origin);
    }

    private boolean isPreflightRequest(HttpServletRequest request) {
        return HttpMethod.OPTIONS.matches(request.getMethod())
                && StringUtils.hasText(request.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD));
    }

    private void applyCorsHeaders(HttpServletRequest request, HttpServletResponse response, String origin) {
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        response.setHeader(HttpHeaders.VARY, joinVaryValues(response.getHeader(HttpHeaders.VARY), HttpHeaders.ORIGIN));

        if (isPreflightRequest(request)) {
            response.setHeader(
                    HttpHeaders.VARY,
                    joinVaryValues(response.getHeader(HttpHeaders.VARY), HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD)
            );
            response.setHeader(
                    HttpHeaders.VARY,
                    joinVaryValues(response.getHeader(HttpHeaders.VARY), HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS)
            );
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, ALLOWED_METHODS);
            response.setHeader(
                    HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                    resolveAllowedHeaders(request.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS))
            );
            response.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, DEFAULT_MAX_AGE_SECONDS);
        }
    }

    private String resolveAllowedHeaders(String requestedHeaders) {
        return StringUtils.hasText(requestedHeaders) ? requestedHeaders : DEFAULT_ALLOWED_HEADERS;
    }

    private String joinVaryValues(String current, String candidate) {
        if (!StringUtils.hasText(current)) {
            return candidate;
        }
        if (current.contains(candidate)) {
            return current;
        }
        return current + ", " + candidate;
    }
}
