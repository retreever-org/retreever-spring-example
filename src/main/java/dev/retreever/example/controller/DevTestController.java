package dev.retreever.example.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/public/test")
public class DevTestController {

    private static final String HTTP_ONLY_COOKIE_NAME = "dev_http_only_cookie";
    private static final String REGULAR_COOKIE_NAME = "dev_regular_cookie";
    private static final Path IMAGE_DIRECTORY = Path.of("images");

    @GetMapping("/cookie/http-only")
    public ResponseEntity<String> issueHttpOnlyCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(HTTP_ONLY_COOKIE_NAME, "http-only-cookie-value")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok("HttpOnly cookie issued");
    }

    @GetMapping("/cookie/regular")
    public ResponseEntity<String> issueRegularCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REGULAR_COOKIE_NAME, "regular-cookie-value")
                .httpOnly(false)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok("Regular cookie issued");
    }

    @GetMapping(value = "/image/png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> issuePng() {
        return imageResponse(MediaType.IMAGE_PNG_VALUE, "img.png");
    }

    @GetMapping(value = "/image/jpeg", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> issueJpeg() {
        return imageResponse(MediaType.IMAGE_JPEG_VALUE, "img2.jpg");
    }

    @GetMapping(value = "/image/gif", produces = MediaType.IMAGE_GIF_VALUE)
    public ResponseEntity<byte[]> issueGif() {
        return imageResponse(MediaType.IMAGE_GIF_VALUE, "img5.gif");
    }

    @GetMapping(value = "/image/bmp", produces = "image/bmp")
    public ResponseEntity<byte[]> issueBmp() {
        return imageResponse("image/bmp", "img7.bmp");
    }

    @GetMapping(value = "/image/svg", produces = "image/svg+xml")
    public ResponseEntity<byte[]> issueSvg() {
        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="320" height="160" viewBox="0 0 320 160">
                  <rect width="320" height="160" rx="18" fill="#0f172a"/>
                  <circle cx="70" cy="80" r="34" fill="#22c55e"/>
                  <rect x="124" y="44" width="146" height="24" rx="12" fill="#38bdf8"/>
                  <rect x="124" y="84" width="112" height="16" rx="8" fill="#e2e8f0"/>
                  <text x="124" y="124" fill="#f8fafc" font-family="Arial, sans-serif" font-size="18">Retreever SVG</text>
                </svg>
                """;

        return imageResponse("image/svg+xml", svg.getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping(value = "/image/webp", produces = "image/webp")
    public ResponseEntity<byte[]> issueWebp() {
        return imageResponse("image/webp", "img3.webp");
    }

    @GetMapping(value = "/image/avif", produces = "image/avif")
    public ResponseEntity<byte[]> issueAvif() {
        return imageResponse("image/avif", "img4.avif");
    }

    @GetMapping(value = "/image/ico", produces = "image/x-icon")
    public ResponseEntity<byte[]> issueIco() {
        return imageResponse("image/x-icon", "img6.ico");
    }

    @GetMapping(value = "/image/favicon", produces = "image/x-icon")
    public ResponseEntity<byte[]> issueFavicon() {
        return imageResponse("image/x-icon", "favicon.ico");
    }

    private ResponseEntity<byte[]> imageResponse(String contentType, String fileName) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
                .body(readImageBytes(fileName));
    }

    private ResponseEntity<byte[]> imageResponse(String contentType, byte[] bytes) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
                .body(bytes);
    }

    private byte[] readImageBytes(String fileName) {
        try {
            return Files.readAllBytes(IMAGE_DIRECTORY.resolve(fileName));
        } catch (IOException ex) {
            throw new UncheckedIOException("Failed to read test image: " + fileName, ex);
        }
    }
}
