package com.fintrack.service;

import com.fintrack.dto.request.CategoryRequestDTO;
import com.fintrack.dto.response.CategoryResponseDTO;
import com.fintrack.enums.TransactionType;

import java.util.List;

public interface CategoryService {
    CategoryResponseDTO createCategory(CategoryRequestDTO dto);
    CategoryResponseDTO getCategoryById(Long id);
    List<CategoryResponseDTO> getCategoriesByUser(Long userId);
    List<CategoryResponseDTO> getCategoriesByUserAndType(Long userId, TransactionType type);
    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto);
    void deleteCategory(Long id);
}
