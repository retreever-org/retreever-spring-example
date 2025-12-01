package dev.retreever.example.service;

import dev.retreever.example.dto.shared.ContentType;
import dev.retreever.example.dto.shared.DownloadFile;
import dev.retreever.example.dto.shared.S3PresignedUpload;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CategoryThumbnailService {
    public S3PresignedUpload presignUploadURL(ContentType contentType, String categoryThumbnailDifferentiator) {
        return null;
    }

    public void confirmUpload(UUID categoryId, String objectKey, String categoryThumbnailDifferentiator) {
    }

    public DownloadFile getThumbnail(UUID imageId) {
        return null;
    }
}
