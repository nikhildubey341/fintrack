package com.fintrack.service.impl;

import com.fintrack.dto.request.CategoryRequestDTO;
import com.fintrack.dto.response.CategoryResponseDTO;
import com.fintrack.entity.Category;
import com.fintrack.entity.User;
import com.fintrack.enums.TransactionType;
import com.fintrack.exception.BadRequestException;
import com.fintrack.exception.ResourceNotFoundException;
import com.fintrack.repository.CategoryRepository;
import com.fintrack.repository.UserRepository;
import com.fintrack.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", dto.getUserId()));

        if (categoryRepository.existsByNameAndUserId(dto.getName(), dto.getUserId())) {
            throw new BadRequestException("Category '" + dto.getName() + "' already exists for this user");
        }

        Category category = Category.builder()
                .name(dto.getName())
                .type(dto.getType())
                .description(dto.getDescription())
                .user(user)
                .build();

        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponseDTO getCategoryById(Long id) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        return mapToResponse(cat);
    }

    @Override
    public List<CategoryResponseDTO> getCategoriesByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }
        return categoryRepository.findByUserId(userId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponseDTO> getCategoriesByUserAndType(Long userId, TransactionType type) {
        return categoryRepository.findByUserIdAndType(userId, type)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category", id);
        }
        categoryRepository.deleteById(id);
    }

    private CategoryResponseDTO mapToResponse(Category c) {
        return CategoryResponseDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .type(c.getType())
                .description(c.getDescription())
                .userId(c.getUser().getId())
                .build();
    }
}
