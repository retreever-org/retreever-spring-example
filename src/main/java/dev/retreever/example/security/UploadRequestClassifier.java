package dev.retreever.example.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class UploadRequestClassifier {

    public int resolveUploadUnits(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return 0;
        }

        String uri = request.getRequestURI();
        if (uri.matches("^/api/v1/products/variants/\\d+/images/presign$")) {
            String uploadCount = request.getParameter("upload_count");
            if (uploadCount == null || uploadCount.isBlank()) {
                return 1;
            }
            return Math.max(Integer.parseInt(uploadCount), 0);
        }

        if ("/api/v1/categories/thumbnail/presign-upload".equals(uri)) {
            return 1;
        }

        return 0;
    }
}
