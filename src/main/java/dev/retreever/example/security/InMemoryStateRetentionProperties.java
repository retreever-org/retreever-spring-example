package dev.retreever.example.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "retreever.example.retention")
public class InMemoryStateRetentionProperties {

    private Duration maxAge = Duration.ofHours(5);

    public Duration getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Duration maxAge) {
        this.maxAge = maxAge;
    }
}
