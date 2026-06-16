package com.fintrack.controller;

import com.fintrack.dto.request.CategoryRequestDTO;
import com.fintrack.dto.response.ApiResponse;
import com.fintrack.dto.response.CategoryResponseDTO;
import com.fintrack.enums.TransactionType;
import com.fintrack.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category Management", description = "APIs for managing transaction categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create a new category")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> createCategory(
            @Valid @RequestBody CategoryRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created", categoryService.createCategory(dto)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Category fetched", categoryService.getCategoryById(id)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all categories for a user")
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getCategoriesByUser(
            @PathVariable Long userId,
            @RequestParam(required = false) TransactionType type) {
        List<CategoryResponseDTO> categories = (type != null)
                ? categoryService.getCategoriesByUserAndType(userId, type)
                : categoryService.getCategoriesByUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Categories fetched", categories));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> updateCategory(
            @PathVariable Long id, @Valid @RequestBody CategoryRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Category updated", categoryService.updateCategory(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted"));
    }
}
