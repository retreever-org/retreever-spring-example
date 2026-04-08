package dev.retreever.example.exception.scenario;

public class ScenarioUnauthorizedException extends RuntimeException {

    public ScenarioUnauthorizedException(String message) {
        super(message);
    }
}
