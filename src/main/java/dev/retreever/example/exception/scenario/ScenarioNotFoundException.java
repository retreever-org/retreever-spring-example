package dev.retreever.example.exception.scenario;

public class ScenarioNotFoundException extends RuntimeException {

    public ScenarioNotFoundException(String message) {
        super(message);
    }
}
