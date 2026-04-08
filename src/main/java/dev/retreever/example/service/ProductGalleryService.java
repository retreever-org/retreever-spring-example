package dev.retreever.example.service;

import dev.retreever.example.dto.shared.ContentType;
import dev.retreever.example.dto.shared.DownloadFile;
import dev.retreever.example.dto.shared.S3PresignedUpload;
import dev.retreever.example.service.support.MockDataFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.lang.Long;

@Service
public class ProductGalleryService {
    private final MockDataFactory mockDataFactory;

    public ProductGalleryService(MockDataFactory mockDataFactory) {
        this.mockDataFactory = mockDataFactory;
    }

    public List<S3PresignedUpload> presignImageUploads(Long variantId, int uploadCount, ContentType contentType, String productGalleryDifferentiator) {
        return java.util.stream.IntStream.range(0, Math.max(uploadCount, 1))
                .mapToObj(index -> mockDataFactory.presignedUpload(
                        productGalleryDifferentiator + "/" + (variantId == null ? 1L : variantId),
                        contentType.value().contains("png") ? ".png" : ".jpg"
                ))
                .toList();
    }

    public void confirmImageUploads(Long variantId, String[] objectKeys, String productGalleryDifferentiator) {
    }

    public DownloadFile getImage(Long imageId) {
        return mockDataFactory.imageFile();
    }

    public void deleteImage(Long imageId) {
    }
}
