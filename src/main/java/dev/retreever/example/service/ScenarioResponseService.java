package dev.retreever.example.service;

import dev.retreever.example.dto.response.ScenarioPayloads;
import dev.retreever.example.exception.scenario.ScenarioBadRequestException;
import dev.retreever.example.exception.scenario.ScenarioForbiddenException;
import dev.retreever.example.exception.scenario.ScenarioInternalServerException;
import dev.retreever.example.exception.scenario.ScenarioNotFoundException;
import dev.retreever.example.exception.scenario.ScenarioUnauthorizedException;
import dev.retreever.example.security.MockAuthenticatedUser;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ScenarioResponseService {

    public ScenarioPayloads.SuccessPayload successPayload() {
        int inventory = randomInt(20, 80);
        int qualityScore = randomInt(85, 99);

        return new ScenarioPayloads.SuccessPayload(
                UUID.randomUUID(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS),
                ScenarioPayloads.ScenarioState.CREATED,
                "Synthetic success payload generated for schema verification.",
                List.of(
                        item("SKU-" + randomInt(1000, 9999), 0.98),
                        item("SKU-" + randomInt(1000, 9999), 0.87)
                ),
                Map.of(
                        "inventory", inventory,
                        "quality_score", qualityScore
                )
        );
    }

    public ScenarioPayloads.CreatedPayload createdPayload() {
        UUID resourceId = UUID.randomUUID();
        return new ScenarioPayloads.CreatedPayload(
                resourceId,
                "/api/v1/scenarios/public/resources/" + resourceId,
                ScenarioPayloads.ScenarioState.CREATED,
                Instant.now().plus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS)
        );
    }

    public ScenarioPayloads.AcceptedPayload acceptedPayload() {
        return new ScenarioPayloads.AcceptedPayload(
                UUID.randomUUID(),
                randomInt(15, 90),
                ScenarioPayloads.ScenarioState.QUEUED,
                List.of("queued", "validated", "awaiting-worker")
        );
    }

    public ScenarioPayloads.RedirectPayload redirectPayload() {
        return new ScenarioPayloads.RedirectPayload(
                "/retreever",
                "Redirecting the caller to the Retreever UI for follow-up inspection.",
                randomInt(5, 30),
                ScenarioPayloads.ScenarioState.REDIRECTED
        );
    }

    public ScenarioPayloads.ValidationProblem validationProblem(ScenarioBadRequestException ex) {
        return new ScenarioPayloads.ValidationProblem(
                "SCENARIO_BAD_REQUEST",
                ex.getMessage(),
                List.of(
                        new ScenarioPayloads.FieldViolation("query.limit", "MIN_VALUE:1", "0"),
                        new ScenarioPayloads.FieldViolation("request.mode", "ALLOWED_VALUES:[safe, strict]", "broken")
                ),
                Instant.now().truncatedTo(ChronoUnit.SECONDS)
        );
    }

    public ScenarioPayloads.NotFoundProblem notFoundProblem(ScenarioNotFoundException ex) {
        return new ScenarioPayloads.NotFoundProblem(
                "SCENARIO_NOT_FOUND",
                "ScenarioResource",
                ex.getMessage(),
                UUID.randomUUID(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS)
        );
    }

    public ScenarioPayloads.AuthorizationProblem unauthorizedProblem(ScenarioUnauthorizedException ex) {
        return new ScenarioPayloads.AuthorizationProblem(
                "SCENARIO_UNAUTHORIZED",
                ex.getMessage(),
                "anonymous",
                "authenticated",
                Instant.now().truncatedTo(ChronoUnit.SECONDS)
        );
    }

    public ScenarioPayloads.AuthorizationProblem forbiddenProblem(ScenarioForbiddenException ex) {
        return new ScenarioPayloads.AuthorizationProblem(
                "SCENARIO_FORBIDDEN",
                ex.getMessage(),
                "seller@quickcart.test",
                "admin",
                Instant.now().truncatedTo(ChronoUnit.SECONDS)
        );
    }

    public ScenarioPayloads.FailureProblem failureProblem(ScenarioInternalServerException ex) {
        return new ScenarioPayloads.FailureProblem(
                "SCENARIO_INTERNAL_ERROR",
                ex.getMessage(),
                UUID.randomUUID(),
                true,
                Map.of(
                        "node", "retreever-example",
                        "phase", "response-lab",
                        "hint", "Synthetic failure raised for documentation coverage."
                )
        );
    }

    public ScenarioPayloads.SecureEcho secureEcho(MockAuthenticatedUser principal) {
        return new ScenarioPayloads.SecureEcho(
                principal.email(),
                principal.deviceId(),
                principal.authorities(),
                List.of("authorization-validated", "device-bound", "roles-resolved"),
                UUID.randomUUID()
        );
    }

    private ScenarioPayloads.ScenarioItem item(String sku, double score) {
        return new ScenarioPayloads.ScenarioItem(
                sku,
                score,
                Map.of(
                        "channel", "api-lab",
                        "tier", score > 0.9 ? "gold" : "silver"
                )
        );
    }

    private int randomInt(int minInclusive, int maxInclusive) {
        return ThreadLocalRandom.current().nextInt(minInclusive, maxInclusive + 1);
    }
}
