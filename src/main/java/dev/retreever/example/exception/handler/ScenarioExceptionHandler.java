package dev.retreever.example.exception.handler;

import dev.retreever.annotation.ApiError;
import dev.retreever.example.dto.response.ScenarioPayloads;
import dev.retreever.example.exception.scenario.ScenarioBadRequestException;
import dev.retreever.example.exception.scenario.ScenarioForbiddenException;
import dev.retreever.example.exception.scenario.ScenarioInternalServerException;
import dev.retreever.example.exception.scenario.ScenarioNotFoundException;
import dev.retreever.example.exception.scenario.ScenarioUnauthorizedException;
import dev.retreever.example.service.ScenarioResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class ScenarioExceptionHandler {

    private final ScenarioResponseService responseService;

    @ApiError(status = HttpStatus.BAD_REQUEST, description = "Synthetic validation error")
    @ExceptionHandler(ScenarioBadRequestException.class)
    public ResponseEntity<ScenarioPayloads.ValidationProblem> handleBadRequest(ScenarioBadRequestException ex) {
        return ResponseEntity.badRequest().body(responseService.validationProblem(ex));
    }

    @ApiError(status = HttpStatus.UNAUTHORIZED, description = "Synthetic unauthorized error")
    @ExceptionHandler(ScenarioUnauthorizedException.class)
    public ResponseEntity<ScenarioPayloads.AuthorizationProblem> handleUnauthorized(ScenarioUnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseService.unauthorizedProblem(ex));
    }

    @ApiError(status = HttpStatus.FORBIDDEN, description = "Synthetic forbidden error")
    @ExceptionHandler(ScenarioForbiddenException.class)
    public ResponseEntity<ScenarioPayloads.AuthorizationProblem> handleForbidden(ScenarioForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseService.forbiddenProblem(ex));
    }

    @ApiError(status = HttpStatus.NOT_FOUND, description = "Synthetic not found error")
    @ExceptionHandler(ScenarioNotFoundException.class)
    public ResponseEntity<ScenarioPayloads.NotFoundProblem> handleNotFound(ScenarioNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseService.notFoundProblem(ex));
    }

    @ApiError(status = HttpStatus.INTERNAL_SERVER_ERROR, description = "Synthetic server failure")
    @ExceptionHandler(ScenarioInternalServerException.class)
    public ResponseEntity<ScenarioPayloads.FailureProblem> handleInternalServerError(ScenarioInternalServerException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseService.failureProblem(ex));
    }
}
