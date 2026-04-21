package dev.retreever.example.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityCorsIntegrationTest {

    private static final String DEV_ORIGIN = "http://localhost:5173";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    @LocalServerPort
    private int port;

    @Test
    void retreeverPingIncludesCorsHeadersForUnauthorizedDevRequests() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/retreever/ping"))
                .header("Origin", DEV_ORIGIN)
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, response.statusCode());
        assertEquals(DEV_ORIGIN, response.headers().firstValue("Access-Control-Allow-Origin").orElse(null));
        assertEquals("true", response.headers().firstValue("Access-Control-Allow-Credentials").orElse(null));
    }
}
