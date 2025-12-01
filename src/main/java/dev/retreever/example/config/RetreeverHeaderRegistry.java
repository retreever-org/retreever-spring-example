package dev.retreever.example.config;

import dev.retreever.endpoint.model.ApiHeader;
import dev.retreever.schema.model.JsonPropertyType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class RetreeverHeaderRegistry {

    @Bean
    ApiHeader authHeader() {
        return new ApiHeader()
                .setName(HttpHeaders.AUTHORIZATION).setRequired(true).setType(JsonPropertyType.STRING)
                .setDescription("Authorization header for API requests");
    }

    @Bean
    ApiHeader deviceHeader() {
        return new ApiHeader()
                .setName("X-Device-ID").setRequired(true).setType(JsonPropertyType.STRING)
                .setDescription("Authorization header for API requests");
    }
}
