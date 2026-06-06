package dev.retreever.example.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityCorsIntegrationTest {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @LocalServerPort
    private int port;

    @Test
    void retreeverPingIsPublicForSameOriginRequests() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/retreever/ping"))
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void rootServesRetreeverWithoutRedirect() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/"))
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("http://localhost:" + port + "/", response.uri().toString());
    }

    @Test
    void staticBrandIconIsPublic() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/assets/icon192v2.png"))
                .GET()
                .build();

        HttpResponse<byte[]> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofByteArray());

        assertEquals(200, response.statusCode());
    }

    @Test
    void firstApiRequestIssuesDeviceCookie() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/v1/public/brands"))
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(extractDeviceCookie(response) != null && !extractDeviceCookie(response).isBlank());
    }

    @Test
    void secureEndpointReturnsShortLoginHintForUnauthorizedRequests() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/v1/scenarios/secure/echo"))
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, response.statusCode());
        assertTrue(response.body().contains(MockIdentityService.defaultDemoLoginHint()));
    }

    @Test
    void adminOnlyEndpointReturnsShortLoginHintForForbiddenRequests() throws Exception {
        LoginSession customerSession = login("customer@quickcart.test", "Passw0rd!");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/v1/scenarios/secure/admin-only"))
                .header("Authorization", "Bearer " + customerSession.accessToken())
                .header("Cookie", customerSession.deviceCookie())
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(403, response.statusCode());
        assertTrue(response.body().contains(MockIdentityService.defaultDemoLoginHint()));
    }

    @Test
    void registerEndpointIgnoresInvalidAuthorizationHeader() throws Exception {
        String formBody = "email=" + urlEncode("new-user@quickcart.test") + "&password=" + urlEncode("Passw0rd!");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/v1/public/users/register"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Bearer invalid-token")
                .POST(HttpRequest.BodyPublishers.ofString(formBody))
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    void loginEndpointIgnoresInvalidAuthorizationHeader() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/v1/public/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Bearer invalid-token")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "email=" + urlEncode(MockIdentityService.DEFAULT_DEMO_EMAIL)
                                + "&password=" + urlEncode(MockIdentityService.DEFAULT_DEMO_PASSWORD)
                ))
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void refreshEndpointIgnoresInvalidAuthorizationHeader() throws Exception {
        LoginSession adminSession = login(
                MockIdentityService.DEFAULT_DEMO_EMAIL,
                MockIdentityService.DEFAULT_DEMO_PASSWORD
        );
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/v1/public/login/refresh"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer invalid-token")
                .header("Cookie", adminSession.deviceCookie())
                .POST(HttpRequest.BodyPublishers.ofString(
                        "{\"refresh_token\":\"" + adminSession.refreshToken() + "\"}"
                ))
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void publicApiRequestsAreLimitedPerDeviceAndIp() throws Exception {
        String deviceCookie = issuePublicDeviceCookie();

        for (int attempt = 0; attempt < 19; attempt++) {
            HttpResponse<String> response = sendPublicBrandsRequest(deviceCookie);
            assertEquals(200, response.statusCode());
            deviceCookie = extractDeviceCookie(response);
        }

        HttpResponse<String> limitedResponse = sendPublicBrandsRequest(deviceCookie);

        assertEquals(429, limitedResponse.statusCode());
        assertTrue(limitedResponse.body().contains("Too Many Requests"));
    }

    @Test
    void uploadPresignVolumeIsLimitedPerDeviceAndIp() throws Exception {
        LoginSession adminSession = login(
                MockIdentityService.DEFAULT_DEMO_EMAIL,
                MockIdentityService.DEFAULT_DEMO_PASSWORD
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/v1/products/variants/1/images/presign?upload_count=3"))
                .header("Authorization", "Bearer " + adminSession.accessToken())
                .header("Cookie", adminSession.deviceCookie())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"content_type\":\"image/png\"}"))
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(429, response.statusCode());
        assertTrue(response.body().contains("Too many upload requests"));
    }

    @Test
    void seededDemoAdminCanLoginWithDefaultCredentials() throws Exception {
        LoginSession adminSession = login(
                MockIdentityService.DEFAULT_DEMO_EMAIL,
                MockIdentityService.DEFAULT_DEMO_PASSWORD
        );

        assertTrue(adminSession.accessToken() != null && !adminSession.accessToken().isBlank());
        assertTrue(adminSession.deviceCookie() != null && !adminSession.deviceCookie().isBlank());
    }

    @Test
    void syntheticUnauthorizedScenarioReturnsShortLoginHint() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/v1/scenarios/public/unauthorized"))
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(401, response.statusCode());
        assertTrue(response.body().contains(MockIdentityService.defaultDemoLoginHint()));
    }

    @Test
    void syntheticForbiddenScenarioReturnsShortLoginHint() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/v1/scenarios/public/forbidden"))
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(403, response.statusCode());
        assertTrue(response.body().contains(MockIdentityService.defaultDemoLoginHint()));
    }

    private LoginSession login(String email, String password) throws Exception {
        String formBody = "email=" + urlEncode(email) + "&password=" + urlEncode(password);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/v1/public/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formBody))
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonNode body = OBJECT_MAPPER.readTree(response.body());
        String accessToken = body.path("data").path("access_token").asText();
        String refreshToken = body.path("data").path("refresh_token").asText();
        String deviceCookie = extractDeviceCookie(response);

        return new LoginSession(accessToken, refreshToken, deviceCookie);
    }

    private String issuePublicDeviceCookie() throws Exception {
        HttpResponse<String> response = sendPublicBrandsRequest(null);
        assertEquals(200, response.statusCode());
        return extractDeviceCookie(response);
    }

    private HttpResponse<String> sendPublicBrandsRequest(String deviceCookie) throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/v1/public/brands"))
                .GET();
        if (deviceCookie != null && !deviceCookie.isBlank()) {
            requestBuilder.header("Cookie", deviceCookie);
        }
        return HTTP_CLIENT.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private String extractDeviceCookie(HttpResponse<?> response) {
        String setCookieHeader = response.headers().firstValue("set-cookie").orElse("");
        return setCookieHeader.isBlank() ? null : setCookieHeader.split(";", 2)[0];
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private record LoginSession(String accessToken, String refreshToken, String deviceCookie) {
    }
}
