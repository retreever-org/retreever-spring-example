package dev.retreever.example.service;

import dev.retreever.example.dto.shared.ContentType;
import dev.retreever.example.dto.shared.DownloadFile;
import dev.retreever.example.dto.shared.S3PresignedUpload;
import dev.retreever.example.service.support.MockDataFactory;
import org.springframework.stereotype.Service;

import java.lang.Long;

@Service
public class CategoryThumbnailService {
    private final MockDataFactory mockDataFactory;

    public CategoryThumbnailService(MockDataFactory mockDataFactory) {
        this.mockDataFactory = mockDataFactory;
    }

    public S3PresignedUpload presignUploadURL(ContentType contentType, String categoryThumbnailDifferentiator) {
        return mockDataFactory.presignedUpload(
                categoryThumbnailDifferentiator,
                contentType.value().contains("png") ? ".png" : ".jpg"
        );
    }

    public void confirmUpload(Long categoryId, String objectKey, String categoryThumbnailDifferentiator) {
    }

    public DownloadFile getThumbnail(Long imageId) {
        return mockDataFactory.imageFile();
    }
}
