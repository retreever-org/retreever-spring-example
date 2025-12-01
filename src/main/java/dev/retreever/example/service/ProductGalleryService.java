package dev.retreever.example.service;

import dev.retreever.example.dto.shared.ContentType;
import dev.retreever.example.dto.shared.DownloadFile;
import dev.retreever.example.dto.shared.S3PresignedUpload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductGalleryService {
    public List<S3PresignedUpload> presignImageUploads(UUID variantId, int uploadCount, ContentType contentType, String productGalleryDifferentiator) {
        return null;
    }

    public void confirmImageUploads(UUID variantId, String[] objectKeys, String productGalleryDifferentiator) {
    }

    public DownloadFile getImage(UUID imageId) {
        return null;
    }

    public void deleteImage(UUID imageId) {
    }
}
