package dev.retreever.example.controller;

import dev.retreever.example.dto.envelope.ApiAck;
import dev.retreever.example.dto.envelope.ApiResponse;
import dev.retreever.example.dto.shared.ContentType;
import dev.retreever.example.dto.shared.DownloadFile;
import dev.retreever.example.dto.shared.S3PresignedUpload;
import dev.retreever.example.service.ProductGalleryService;
import lombok.AllArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.lang.Long;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class ProductGalleryController {
    private static final String PRODUCT_GALLERY_DIFFERENTIATOR = "product_gallery";

    private final ProductGalleryService productGalleryService;

    @PreAuthorize("hasAnyAuthority('seller','admin')")
    @PostMapping("/products/variants/{variantId}/images/presign")
    public ResponseEntity<ApiResponse<List<S3PresignedUpload>>> presignImageUploads(
            @PathVariable Long variantId,
            @RequestParam("upload_count") int uploadCount,
            @RequestBody ContentType contentType
            ) {
        contentType.mustStartWith("image/");
        List<S3PresignedUpload> response = productGalleryService.presignImageUploads(variantId, uploadCount, contentType, PRODUCT_GALLERY_DIFFERENTIATOR);
        return ResponseEntity.ok(ApiResponse.success(
                "Generated presigned-URLs for product images upload",
                response
        ));
    }

    @PreAuthorize("hasAnyAuthority('seller','admin')")
    @PostMapping("/products/variants/{variantId}/images/confirm-upload")
    public ResponseEntity<ApiAck> confirmImageUploads(
            @PathVariable Long variantId,
            @RequestParam String[] objectKeys
            ) {
        productGalleryService.confirmImageUploads(variantId, objectKeys, PRODUCT_GALLERY_DIFFERENTIATOR);
        return ResponseEntity.ok(ApiAck.success("Image upload completed successfully"));
    }

    @GetMapping("/public/products/variants/images/{imageId}")
    public ResponseEntity<byte[]> getImageUploads(
            @PathVariable Long imageId
            ) {
        DownloadFile file = productGalleryService.getImage(imageId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.contentType()))
                .cacheControl(CacheControl
                        .maxAge(7, TimeUnit.DAYS)         // Browser cache: 7 days
                        .sMaxAge(365, TimeUnit.DAYS)     // CDN cache: 1 year
                        .cachePublic()
                        .immutable())                            // Optional: treats as versioned resource
                .body(file.bytes());
    }

    @PreAuthorize("hasAnyAuthority('seller','admin')")
    @DeleteMapping("/variants/images/{imageId}")
    public ResponseEntity<ApiAck> deleteImageUploads(
            @PathVariable Long imageId
            ) {
        productGalleryService.deleteImage(imageId);
        return ResponseEntity.ok(ApiAck.success("Image deleted successfully"));
    }

}
