package dev.retreever.example.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.retreever.example.dto.envelope.ApiErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiRequestRateLimitFilter extends OncePerRequestFilter {

    private final MockDeviceCookieService deviceCookieService;
    private final ApiRateLimiter apiRateLimiter;
    private final UploadRequestClassifier uploadRequestClassifier;
    private final ObjectMapper objectMapper;

    public ApiRequestRateLimitFilter(
            MockDeviceCookieService deviceCookieService,
            ApiRateLimiter apiRateLimiter,
            UploadRequestClassifier uploadRequestClassifier,
            ObjectMapper objectMapper
    ) {
        this.deviceCookieService = deviceCookieService;
        this.apiRateLimiter = apiRateLimiter;
        this.uploadRequestClassifier = uploadRequestClassifier;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/v1")
                || "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        ResolvedDevice resolvedDevice = deviceCookieService.resolveOrCreateDevice(request, response);
        DeviceRequestIdentity identity = new DeviceRequestIdentity(resolvedDevice.deviceId(), resolvedDevice.clientIp());

        RateLimitDecision requestDecision = apiRateLimiter.validateRequest(identity);
        if (!requestDecision.allowed()) {
            writeTooManyRequests(response, requestDecision);
            return;
        }

        int uploadUnits = resolveUploadUnits(request);
        if (uploadUnits > 0) {
            RateLimitDecision uploadDecision = apiRateLimiter.validateUpload(identity, uploadUnits);
            if (!uploadDecision.allowed()) {
                writeTooManyRequests(response, uploadDecision);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private int resolveUploadUnits(HttpServletRequest request) {
        try {
            return uploadRequestClassifier.resolveUploadUnits(request);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private void writeTooManyRequests(HttpServletResponse response, RateLimitDecision decision) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(
                response.getOutputStream(),
                ApiErrorResponse.build("Too Many Requests", decision.rejectionReason())
        );
    }
}
