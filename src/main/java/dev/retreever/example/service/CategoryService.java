package dev.retreever.example.service;

import dev.retreever.example.dto.request.CategoryCreateRequest;
import dev.retreever.example.dto.request.CategoryUpdateRequest;
import dev.retreever.example.dto.response.CategoryDetail;
import dev.retreever.example.dto.response.CategorySummary;
import dev.retreever.example.dto.shared.CategoryStatus;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.lang.Long;

@Service
public class CategoryService {
    public Long createCategory(@Valid CategoryCreateRequest request, Long parentId) {
        return null;
    }

    public CategorySummary getCategory(Long categoryId) {
        return null;
    }

    public List<CategoryDetail> getCategoryCatalogue() {
        return null;
    }

    public List<CategoryDetail> getCategoriesOfAllStatus() {
        return null;
    }

    public CategorySummary updateCategory(Long categoryId, @Valid CategoryUpdateRequest request) {
        return null;
    }

    public CategorySummary updateCategoryStatus(Long categoryId, CategoryStatus categoryStatus) {
        return null;
    }

    public CategorySummary updateCategoryParent(Long categoryId, Long parentId) {
        return null;
    }
}
