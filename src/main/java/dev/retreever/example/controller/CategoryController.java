package dev.retreever.example.controller;

import dev.retreever.example.dto.envelope.ApiAck;
import dev.retreever.example.dto.envelope.ApiResponse;
import dev.retreever.example.dto.request.CategoryCreateRequest;
import dev.retreever.example.dto.request.CategoryUpdateRequest;
import dev.retreever.example.dto.response.CategoryDetail;
import dev.retreever.example.dto.response.CategorySummary;
import dev.retreever.example.dto.shared.CategoryStatus;
import dev.retreever.example.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class CategoryController {
    public static final String CATEGORY_THUMBNAIL_DIFFERENTIATOR = "category_thumbnail";

    private final CategoryService categoryService;

    @PostMapping("/categories")
    public ResponseEntity<ApiAck> createCategory(
            @Valid @RequestBody CategoryCreateRequest request,
            @RequestParam(required = false) UUID parentId
            ) {
        UUID id = categoryService.createCategory(request, parentId);
        return ResponseEntity
                .created(URI.create("/api/v1/categories/" + id))
                .body(ApiAck.success("Category Created"));
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<CategorySummary>> getCategory(
            @PathVariable UUID categoryId
            ) {
        CategorySummary summary = categoryService.getCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success("Category Retrieved", summary));
    }

    @GetMapping("/public/categories")
    public ResponseEntity<ApiResponse<List<CategoryDetail>>> getCategoryCatalogue() {
        List<CategoryDetail> catalogue = categoryService.getCategoryCatalogue();
        return ResponseEntity.ok(ApiResponse.success("Category Catalogue Retrieved", catalogue));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryDetail>>> getCategoriesOfAllStatus() {
        List<CategoryDetail> catalogue = categoryService.getCategoriesOfAllStatus();
        return ResponseEntity.ok(ApiResponse.success("Category Catalogue Retrieved", catalogue));
    }

    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<CategorySummary>> updateCategory(
            @PathVariable UUID categoryId,
            @Valid @RequestBody CategoryUpdateRequest request
    ) {
        CategorySummary summary = categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(ApiResponse.success("Category Updated", summary));
    }

    @PatchMapping("/categories/{categoryId}/status")
    public ResponseEntity<ApiResponse<CategorySummary>> updateCategoryStatus(
            @PathVariable UUID categoryId,
            @RequestParam("value") CategoryStatus categoryStatus
            ) {
        CategorySummary summary = categoryService.updateCategoryStatus(categoryId, categoryStatus);
        return ResponseEntity.ok(ApiResponse.success("Category Summary Updated", summary));
    }

    @PatchMapping("/categories/{categoryId}/parent")
    public ResponseEntity<ApiResponse<CategorySummary>> updateCategoryParent(
            @PathVariable UUID categoryId,
            @RequestParam("parent_id") UUID parentId
            ) {
        CategorySummary summary = categoryService.updateCategoryParent(categoryId, parentId);
        return ResponseEntity.ok(ApiResponse.success("Category Parent Updated", summary));
    }
}
