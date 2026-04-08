package dev.retreever.example.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ScenarioPayloads {

    private ScenarioPayloads() {
    }

    public enum ScenarioState {
        CREATED,
        QUEUED,
        REDIRECTED,
        FAILED
    }

    public record SuccessPayload(
            @JsonProperty("trace_id") UUID traceId,
            @JsonProperty("generated_at") Instant generatedAt,
            @JsonProperty("state") ScenarioState state,
            @JsonProperty("message") String message,
            @JsonProperty("items") List<ScenarioItem> items,
            @JsonProperty("metrics") Map<String, Integer> metrics
    ) {
    }

    public record CreatedPayload(
            @JsonProperty("resource_id") UUID resourceId,
            @JsonProperty("location") String location,
            @JsonProperty("state") ScenarioState state,
            @JsonProperty("expires_at") Instant expiresAt
    ) {
    }

    public record AcceptedPayload(
            @JsonProperty("job_id") UUID jobId,
            @JsonProperty("estimated_wait_seconds") int estimatedWaitSeconds,
            @JsonProperty("state") ScenarioState state,
            @JsonProperty("checks") List<String> checks
    ) {
    }

    public record RedirectPayload(
            @JsonProperty("target_uri") String targetUri,
            @JsonProperty("reason") String reason,
            @JsonProperty("cooldown_seconds") int cooldownSeconds,
            @JsonProperty("state") ScenarioState state
    ) {
    }

    public record ValidationProblem(
            @JsonProperty("error_code") String errorCode,
            @JsonProperty("message") String message,
            @JsonProperty("violations") List<FieldViolation> violations,
            @JsonProperty("occurred_at") Instant occurredAt
    ) {
    }

    public record NotFoundProblem(
            @JsonProperty("error_code") String errorCode,
            @JsonProperty("resource_type") String resourceType,
            @JsonProperty("lookup_key") String lookupKey,
            @JsonProperty("trace_id") UUID traceId,
            @JsonProperty("occurred_at") Instant occurredAt
    ) {
    }

    public record AuthorizationProblem(
            @JsonProperty("error_code") String errorCode,
            @JsonProperty("message") String message,
            @JsonProperty("actor") String actor,
            @JsonProperty("required_authority") String requiredAuthority,
            @JsonProperty("occurred_at") Instant occurredAt
    ) {
    }

    public record FailureProblem(
            @JsonProperty("error_code") String errorCode,
            @JsonProperty("message") String message,
            @JsonProperty("support_id") UUID supportId,
            @JsonProperty("retryable") boolean retryable,
            @JsonProperty("diagnostics") Map<String, String> diagnostics
    ) {
    }

    public record SecureEcho(
            @JsonProperty("actor") String actor,
            @JsonProperty("device_id") String deviceId,
            @JsonProperty("authorities") Set<String> authorities,
            @JsonProperty("checks") List<String> checks,
            @JsonProperty("correlation_id") UUID correlationId
    ) {
    }

    public record ScenarioItem(
            @JsonProperty("sku") String sku,
            @JsonProperty("score") double score,
            @JsonProperty("labels") Map<String, String> labels
    ) {
    }

    public record FieldViolation(
            @JsonProperty("field") String field,
            @JsonProperty("rule") String rule,
            @JsonProperty("rejected_value") String rejectedValue
    ) {
    }
}
