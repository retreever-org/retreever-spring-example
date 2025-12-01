package dev.retreever.example.service;

import dev.retreever.example.dto.request.CategoryCreateRequest;
import dev.retreever.example.dto.request.CategoryUpdateRequest;
import dev.retreever.example.dto.response.CategoryDetail;
import dev.retreever.example.dto.response.CategorySummary;
import dev.retreever.example.dto.shared.CategoryStatus;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
    public UUID createCategory(@Valid CategoryCreateRequest request, UUID parentId) {
        return null;
    }

    public CategorySummary getCategory(UUID categoryId) {
        return null;
    }

    public List<CategoryDetail> getCategoryCatalogue() {
        return null;
    }

    public List<CategoryDetail> getCategoriesOfAllStatus() {
        return null;
    }

    public CategorySummary updateCategory(UUID categoryId, @Valid CategoryUpdateRequest request) {
        return null;
    }

    public CategorySummary updateCategoryStatus(UUID categoryId, CategoryStatus categoryStatus) {
        return null;
    }

    public CategorySummary updateCategoryParent(UUID categoryId, UUID parentId) {
        return null;
    }
}
