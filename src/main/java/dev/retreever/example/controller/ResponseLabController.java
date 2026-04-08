package dev.retreever.example.controller;

import dev.retreever.annotation.ApiEndpoint;
import dev.retreever.annotation.ApiGroup;
import dev.retreever.example.dto.response.ScenarioPayloads;
import dev.retreever.example.exception.scenario.ScenarioBadRequestException;
import dev.retreever.example.exception.scenario.ScenarioForbiddenException;
import dev.retreever.example.exception.scenario.ScenarioInternalServerException;
import dev.retreever.example.exception.scenario.ScenarioNotFoundException;
import dev.retreever.example.exception.scenario.ScenarioUnauthorizedException;
import dev.retreever.example.security.MockAuthenticatedUser;
import dev.retreever.example.service.ScenarioResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@ApiGroup(
        name = "Response Lab APIs",
        description = "Dedicated endpoints for exercising varied status codes, payload shapes, and security outcomes."
)
@RestController
@RequestMapping("/api/v1/scenarios")
@RequiredArgsConstructor
public class ResponseLabController {

    private final ScenarioResponseService responseService;

    @GetMapping("/public/ok")
    public ResponseEntity<ScenarioPayloads.SuccessPayload> getSuccessScenario() {
        return ResponseEntity.ok(responseService.successPayload());
    }

    @GetMapping("/public/created")
    public ResponseEntity<ScenarioPayloads.CreatedPayload> getCreatedScenario() {
        ScenarioPayloads.CreatedPayload payload = responseService.createdPayload();
        return ResponseEntity.created(URI.create(payload.location())).body(payload);
    }

    @GetMapping("/public/accepted")
    public ResponseEntity<ScenarioPayloads.AcceptedPayload> getAcceptedScenario() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseService.acceptedPayload());
    }

    @GetMapping("/public/redirect")
    public ResponseEntity<ScenarioPayloads.RedirectPayload> getRedirectScenario() {
        ScenarioPayloads.RedirectPayload payload = responseService.redirectPayload();
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(payload.targetUri()))
                .body(payload);
    }

    @ApiEndpoint(
            name = "Synthetic Bad Request",
            description = "Returns a typed 400 payload with field-level violations.",
            errors = ScenarioBadRequestException.class
    )
    @GetMapping("/public/bad-request")
    public ResponseEntity<Void> getBadRequestScenario() {
        throw new ScenarioBadRequestException("The supplied scenario parameters are intentionally invalid.");
    }

    @ApiEndpoint(
            name = "Synthetic Unauthorized",
            description = "Returns a typed 401 payload without using the security filter.",
            errors = ScenarioUnauthorizedException.class
    )
    @GetMapping("/public/unauthorized")
    public ResponseEntity<Void> getUnauthorizedScenario() {
        throw new ScenarioUnauthorizedException("This scenario simulates a failed authentication exchange.");
    }

    @ApiEndpoint(
            name = "Synthetic Forbidden",
            description = "Returns a typed 403 payload for explicit authorization coverage.",
            errors = ScenarioForbiddenException.class
    )
    @GetMapping("/public/forbidden")
    public ResponseEntity<Void> getForbiddenScenario() {
        throw new ScenarioForbiddenException("This scenario simulates a caller lacking the required authority.");
    }

    @ApiEndpoint(
            name = "Synthetic Not Found",
            description = "Returns a typed 404 payload for missing resource coverage.",
            errors = ScenarioNotFoundException.class
    )
    @GetMapping("/public/not-found")
    public ResponseEntity<Void> getNotFoundScenario() {
        throw new ScenarioNotFoundException("resource=" + URI.create("/api/v1/scenarios/public/not-found"));
    }

    @ApiEndpoint(
            name = "Synthetic Internal Error",
            description = "Returns a typed 500 payload for failure-path coverage.",
            errors = ScenarioInternalServerException.class
    )
    @GetMapping("/public/server-error")
    public ResponseEntity<Void> getInternalErrorScenario() {
        throw new ScenarioInternalServerException("Synthetic server error raised to validate 500-response handling.");
    }

    @ApiEndpoint(
            name = "Secure Echo",
            description = "Requires any authenticated caller and echoes the resolved mock security context.",
            secured = true,
            headers = {HttpHeaders.AUTHORIZATION, "X-Device-ID"}
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/secure/echo")
    public ResponseEntity<ScenarioPayloads.SecureEcho> getSecureEcho(Authentication authentication) {
        MockAuthenticatedUser principal = (MockAuthenticatedUser) authentication.getPrincipal();
        return ResponseEntity.ok(responseService.secureEcho(principal));
    }

    @ApiEndpoint(
            name = "Admin Only Echo",
            description = "Requires the admin authority and can be used to exercise real 401/403 security failures.",
            secured = true,
            headers = {HttpHeaders.AUTHORIZATION, "X-Device-ID"}
    )
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/secure/admin-only")
    public ResponseEntity<ScenarioPayloads.SecureEcho> getAdminOnlyEcho(Authentication authentication) {
        MockAuthenticatedUser principal = (MockAuthenticatedUser) authentication.getPrincipal();
        return ResponseEntity.ok(responseService.secureEcho(principal));
    }
}
