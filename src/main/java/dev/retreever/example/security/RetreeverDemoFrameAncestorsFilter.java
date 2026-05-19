package dev.retreever.example.security;

import dev.retreever.api.config.RetreeverSecurityHeadersFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class RetreeverDemoFrameAncestorsFilter extends OncePerRequestFilter {

    private static final String DEFAULT_FRAME_ANCESTORS = "frame-ancestors 'self'";

    private final String frameAncestors;

    public RetreeverDemoFrameAncestorsFilter(String frameAncestors) {
        this.frameAncestors = frameAncestors;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !StringUtils.hasText(frameAncestors);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        applyContentSecurityPolicy(response);
        filterChain.doFilter(request, response);
        applyContentSecurityPolicy(response);
    }

    private void applyContentSecurityPolicy(HttpServletResponse response) {
        if (response.isCommitted()) {
            return;
        }

        response.setHeader(
                "Content-Security-Policy",
                RetreeverSecurityHeadersFilter.CONTENT_SECURITY_POLICY.replace(
                        DEFAULT_FRAME_ANCESTORS,
                        "frame-ancestors 'self' " + frameAncestors.trim()
                )
        );
    }
}
