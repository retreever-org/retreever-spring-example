package dev.retreever.example.service;

import dev.retreever.example.dto.request.CategoryCreateRequest;
import dev.retreever.example.dto.request.CategoryUpdateRequest;
import dev.retreever.example.dto.response.CategoryDetail;
import dev.retreever.example.dto.response.CategorySummary;
import dev.retreever.example.dto.shared.CategoryStatus;
import dev.retreever.example.service.support.MockDataFactory;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.lang.Long;

@Service
public class CategoryService {
    private final MockDataFactory mockDataFactory;

    public CategoryService(MockDataFactory mockDataFactory) {
        this.mockDataFactory = mockDataFactory;
    }

    public Long createCategory(@Valid CategoryCreateRequest request, Long parentId) {
        return mockDataFactory.nextId();
    }

    public CategorySummary getCategory(Long categoryId) {
        return mockDataFactory.categorySummary(categoryId == null ? 1L : categoryId, CategoryStatus.ACTIVE);
    }

    public List<CategoryDetail> getCategoryCatalogue() {
        return List.of(
                mockDataFactory.categoryDetail(1L, CategoryStatus.ACTIVE, 1),
                mockDataFactory.categoryDetail(2L, CategoryStatus.ACTIVE, 1)
        );
    }

    public List<CategoryDetail> getCategoriesOfAllStatus() {
        return List.of(
                mockDataFactory.categoryDetail(1L, CategoryStatus.ACTIVE, 1),
                mockDataFactory.categoryDetail(2L, CategoryStatus.DRAFT, 0),
                mockDataFactory.categoryDetail(3L, CategoryStatus.ARCHIVE, 0)
        );
    }

    public CategorySummary updateCategory(Long categoryId, @Valid CategoryUpdateRequest request) {
        CategoryStatus status = request.categoryStatus() == null ? CategoryStatus.ACTIVE : request.categoryStatus();
        return mockDataFactory.categorySummary(categoryId == null ? 1L : categoryId, status);
    }

    public CategorySummary updateCategoryStatus(Long categoryId, CategoryStatus categoryStatus) {
        return mockDataFactory.categorySummary(categoryId == null ? 1L : categoryId,
                categoryStatus == null ? CategoryStatus.DRAFT : categoryStatus);
    }

    public CategorySummary updateCategoryParent(Long categoryId, Long parentId) {
        return mockDataFactory.categorySummary(categoryId == null ? 1L : categoryId, CategoryStatus.ACTIVE);
    }
}
