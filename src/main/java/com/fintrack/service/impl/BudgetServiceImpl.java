package com.fintrack.service.impl;

import com.fintrack.dto.request.BudgetRequestDTO;
import com.fintrack.dto.response.BudgetResponseDTO;
import com.fintrack.entity.Budget;
import com.fintrack.entity.Category;
import com.fintrack.entity.User;
import com.fintrack.exception.BadRequestException;
import com.fintrack.exception.ResourceNotFoundException;
import com.fintrack.repository.BudgetRepository;
import com.fintrack.repository.CategoryRepository;
import com.fintrack.repository.UserRepository;
import com.fintrack.service.BudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public BudgetResponseDTO createBudget(BudgetRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", dto.getUserId()));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", dto.getCategoryId()));

        // Check if budget already exists for this month/year/category
        if (budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
                dto.getUserId(), dto.getCategoryId(), dto.getMonth(), dto.getYear()).isPresent()) {
            throw new BadRequestException("Budget already set for this category in " +
                    dto.getMonth() + "/" + dto.getYear());
        }

        Budget budget = Budget.builder()
                .amountLimit(dto.getAmountLimit())
                .spentAmount(BigDecimal.ZERO)
                .month(dto.getMonth())
                .year(dto.getYear())
                .user(user)
                .category(category)
                .build();

        return mapToResponse(budgetRepository.save(budget));
    }

    @Override
    public List<BudgetResponseDTO> getBudgetsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }
        return budgetRepository.findByUserId(userId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<BudgetResponseDTO> getBudgetsByUserAndMonth(Long userId, int month, int year) {
        return budgetRepository.findByUserIdAndMonthAndYear(userId, month, year)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BudgetResponseDTO updateBudget(Long id, BudgetRequestDTO dto) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", id));
        budget.setAmountLimit(dto.getAmountLimit());
        return mapToResponse(budgetRepository.save(budget));
    }

    @Override
    public List<BudgetResponseDTO> getOverspendingAlerts(Long userId, int month, int year) {
        return budgetRepository.findOverspentBudgets(userId, month, year)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<BudgetResponseDTO> getNearLimitAlerts(Long userId, int month, int year) {
        return budgetRepository.findNearLimitBudgets(userId, month, year)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteBudget(Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Budget", id);
        }
        budgetRepository.deleteById(id);
    }

    private BudgetResponseDTO mapToResponse(Budget b) {
        BigDecimal remaining = b.getAmountLimit().subtract(b.getSpentAmount());
        double usagePct = 0.0;
        if (b.getAmountLimit().compareTo(BigDecimal.ZERO) > 0) {
            usagePct = b.getSpentAmount()
                    .divide(b.getAmountLimit(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }
        return BudgetResponseDTO.builder()
                .id(b.getId())
                .amountLimit(b.getAmountLimit())
                .spentAmount(b.getSpentAmount())
                .remainingAmount(remaining)
                .usagePercentage(usagePct)
                .month(b.getMonth())
                .year(b.getYear())
                .userId(b.getUser().getId())
                .categoryId(b.getCategory().getId())
                .categoryName(b.getCategory().getName())
                .isOverBudget(b.getSpentAmount().compareTo(b.getAmountLimit()) >= 0)
                .build();
    }
}
