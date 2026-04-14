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
import org.springframework.http.MediaType;
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

    @ApiEndpoint(
            name = "Synthetic Success",
            description = "Returns a standard 200 JSON payload for baseline success-path verification."
    )
    @GetMapping("/public/ok")
    public ResponseEntity<ScenarioPayloads.SuccessPayload> getSuccessScenario() {
        return ResponseEntity.ok(responseService.successPayload());
    }

    @ApiEndpoint(
            name = "Synthetic Created",
            description = "Returns a 201 JSON payload with a Location header for resource-creation testing.",
            status = HttpStatus.CREATED
    )
    @GetMapping("/public/created")
    public ResponseEntity<ScenarioPayloads.CreatedPayload> getCreatedScenario() {
        ScenarioPayloads.CreatedPayload payload = responseService.createdPayload();
        return ResponseEntity.created(URI.create(payload.location())).body(payload);
    }

    @ApiEndpoint(
            name = "Synthetic Accepted",
            description = "Returns a 202 JSON payload that simulates async job acceptance.",
            status = HttpStatus.ACCEPTED
    )
    @GetMapping("/public/accepted")
    public ResponseEntity<ScenarioPayloads.AcceptedPayload> getAcceptedScenario() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseService.acceptedPayload());
    }

    @ApiEndpoint(
            name = "Synthetic Redirect",
            description = "Returns a 302 payload with a Location header for redirect-handling tests.",
            status = HttpStatus.FOUND
    )
    @GetMapping("/public/redirect")
    public ResponseEntity<ScenarioPayloads.RedirectPayload> getRedirectScenario() {
        ScenarioPayloads.RedirectPayload payload = responseService.redirectPayload();
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(payload.targetUri()))
                .body(payload);
    }

    @ApiEndpoint(
            name = "HTML Showcase",
            description = "Returns a browser-friendly HTML document with inline CSS so non-JSON response rendering can be tested."
    )
    @GetMapping(value = "/public/html-showcase", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getHtmlShowcase() {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body("""
                        <!DOCTYPE html>
                        <html lang="en">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Retreever Response Lab</title>
                            <style>
                                :root {
                                    color-scheme: light;
                                    --bg: #081120;
                                    --panel: rgba(8, 17, 32, 0.72);
                                    --line: rgba(255, 255, 255, 0.16);
                                    --text: #f8fbff;
                                    --muted: #bad4ea;
                                    --accent: #68e1fd;
                                    --accent-strong: #ff8fab;
                                    --glow: rgba(104, 225, 253, 0.28);
                                }

                                * {
                                    box-sizing: border-box;
                                }

                                body {
                                    margin: 0;
                                    min-height: 100vh;
                                    font-family: "Segoe UI", "Trebuchet MS", sans-serif;
                                    color: var(--text);
                                    background:
                                        radial-gradient(circle at top left, rgba(255, 143, 171, 0.34), transparent 28%),
                                        radial-gradient(circle at 80% 20%, rgba(104, 225, 253, 0.30), transparent 24%),
                                        linear-gradient(135deg, #07101d 0%, #102748 45%, #071829 100%);
                                    display: grid;
                                    place-items: center;
                                    padding: 32px;
                                }

                                .frame {
                                    width: min(920px, 100%);
                                    border: 1px solid var(--line);
                                    border-radius: 28px;
                                    overflow: hidden;
                                    background: var(--panel);
                                    backdrop-filter: blur(18px);
                                    box-shadow: 0 24px 70px rgba(0, 0, 0, 0.42);
                                }

                                .hero {
                                    padding: 56px 56px 32px;
                                    position: relative;
                                }

                                .badge {
                                    display: inline-flex;
                                    align-items: center;
                                    gap: 10px;
                                    padding: 10px 16px;
                                    border-radius: 999px;
                                    background: rgba(255, 255, 255, 0.08);
                                    border: 1px solid rgba(255, 255, 255, 0.14);
                                    color: var(--muted);
                                    letter-spacing: 0.08em;
                                    text-transform: uppercase;
                                    font-size: 12px;
                                }

                                h1 {
                                    margin: 22px 0 14px;
                                    font-size: clamp(2.6rem, 7vw, 4.8rem);
                                    line-height: 0.95;
                                }

                                p {
                                    margin: 0;
                                    max-width: 56ch;
                                    color: var(--muted);
                                    font-size: 1.05rem;
                                    line-height: 1.7;
                                }

                                .grid {
                                    display: grid;
                                    gap: 18px;
                                    grid-template-columns: repeat(auto-fit, minmax(210px, 1fr));
                                    padding: 0 56px 56px;
                                }

                                .card {
                                    padding: 22px;
                                    border-radius: 22px;
                                    border: 1px solid rgba(255, 255, 255, 0.12);
                                    background: linear-gradient(180deg, rgba(255, 255, 255, 0.10), rgba(255, 255, 255, 0.04));
                                    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.12);
                                }

                                .card strong {
                                    display: block;
                                    margin-bottom: 8px;
                                    font-size: 1.02rem;
                                }

                                .pulse {
                                    margin-top: 26px;
                                    width: 140px;
                                    height: 140px;
                                    border-radius: 50%;
                                    background: radial-gradient(circle, var(--glow), transparent 60%);
                                    position: absolute;
                                    right: 36px;
                                    top: 30px;
                                    filter: blur(8px);
                                }

                                .cta {
                                    display: inline-block;
                                    margin-top: 28px;
                                    padding: 14px 20px;
                                    border-radius: 14px;
                                    text-decoration: none;
                                    color: #07101d;
                                    font-weight: 700;
                                    background: linear-gradient(90deg, var(--accent), #b8fff1);
                                }

                                .footer {
                                    border-top: 1px solid rgba(255, 255, 255, 0.10);
                                    padding: 18px 56px 24px;
                                    color: var(--muted);
                                    font-size: 0.95rem;
                                }
                            </style>
                        </head>
                        <body>
                            <main class="frame">
                                <section class="hero">
                                    <div class="pulse"></div>
                                    <span class="badge">Retreever Response Lab</span>
                                    <h1>HTML that feels alive.</h1>
                                    <p>
                                        This endpoint intentionally returns a full HTML document with inline CSS so you can
                                        test browser rendering, preview cards, and non-JSON response handling in one shot.
                                    </p>
                                    <a class="cta" href="/api/v1/scenarios/public/xml-showcase">Open the XML companion</a>
                                </section>
                                <section class="grid">
                                    <article class="card">
                                        <strong>Content-Type</strong>
                                        <span>text/html</span>
                                    </article>
                                    <article class="card">
                                        <strong>Visual Style</strong>
                                        <span>Glassmorphism panel, aurora gradients, and layered highlights.</span>
                                    </article>
                                    <article class="card">
                                        <strong>Use Case</strong>
                                        <span>Quick validation for clients, SDKs, browsers, proxies, and preview tooling.</span>
                                    </article>
                                </section>
                                <div class="footer">Try the paired XML endpoint at <code>/api/v1/scenarios/public/xml-showcase</code>.</div>
                            </main>
                        </body>
                        </html>
                        """);
    }

    @ApiEndpoint(
            name = "XML Showcase",
            description = "Returns an application/xml document so XML parsers and content negotiation paths can be verified."
    )
    @GetMapping(value = "/public/xml-showcase", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getXmlShowcase() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body("""
                        <?xml version="1.0" encoding="UTF-8"?>
                        <labResponse>
                            <title>Retreever XML Showcase</title>
                            <status>success</status>
                            <mediaType>application/xml</mediaType>
                            <summary>Sample XML payload for validating non-JSON API responses.</summary>
                            <palette>
                                <color role="background">#081120</color>
                                <color role="accent">#68E1FD</color>
                                <color role="accent-strong">#FF8FAB</color>
                            </palette>
                            <links>
                                <endpoint rel="self">/api/v1/scenarios/public/xml-showcase</endpoint>
                                <endpoint rel="html">/api/v1/scenarios/public/html-showcase</endpoint>
                            </links>
                        </labResponse>
                        """);
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
