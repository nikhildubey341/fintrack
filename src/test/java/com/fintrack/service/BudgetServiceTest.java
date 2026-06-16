package com.fintrack.service;

import com.fintrack.dto.request.BudgetRequestDTO;
import com.fintrack.dto.response.BudgetResponseDTO;
import com.fintrack.entity.Budget;
import com.fintrack.entity.Category;
import com.fintrack.entity.User;
import com.fintrack.enums.TransactionType;
import com.fintrack.exception.BadRequestException;
import com.fintrack.repository.BudgetRepository;
import com.fintrack.repository.CategoryRepository;
import com.fintrack.repository.UserRepository;
import com.fintrack.service.impl.BudgetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Budget Service Tests")
class BudgetServiceTest {

    @Mock private BudgetRepository budgetRepository;
    @Mock private UserRepository userRepository;
    @Mock private CategoryRepository categoryRepository;

    @InjectMocks
    private BudgetServiceImpl budgetService;

    private User mockUser;
    private Category mockCategory;
    private BudgetRequestDTO mockRequestDTO;

    @BeforeEach
    void setUp() {
        mockUser = User.builder().id(1L).name("Nikhil").email("nikhil@test.com").phone("9876543210").build();
        mockCategory = Category.builder().id(1L).name("Food").type(TransactionType.EXPENSE).user(mockUser).build();

        mockRequestDTO = BudgetRequestDTO.builder()
                .amountLimit(new BigDecimal("5000.00"))
                .month(6)
                .year(2026)
                .userId(1L)
                .categoryId(1L)
                .build();
    }

    @Test
    @DisplayName("Should create budget successfully")
    void shouldCreateBudgetSuccessfully() {
        Budget savedBudget = Budget.builder()
                .id(1L)
                .amountLimit(new BigDecimal("5000.00"))
                .spentAmount(BigDecimal.ZERO)
                .month(6)
                .year(2026)
                .user(mockUser)
                .category(mockCategory)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(1L, 1L, 6, 2026))
                .thenReturn(Optional.empty());
        when(budgetRepository.save(any(Budget.class))).thenReturn(savedBudget);

        BudgetResponseDTO result = budgetService.createBudget(mockRequestDTO);

        assertNotNull(result);
        assertEquals(new BigDecimal("5000.00"), result.getAmountLimit());
        assertEquals(BigDecimal.ZERO, result.getSpentAmount());
        assertFalse(result.getIsOverBudget());
        verify(budgetRepository, times(1)).save(any(Budget.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when budget already exists")
    void shouldThrowExceptionWhenBudgetAlreadyExists() {
        Budget existing = Budget.builder().id(1L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(1L, 1L, 6, 2026))
                .thenReturn(Optional.of(existing));

        assertThrows(BadRequestException.class,
                () -> budgetService.createBudget(mockRequestDTO));
    }

    @Test
    @DisplayName("Should correctly calculate usage percentage")
    void shouldCalculateUsagePercentageCorrectly() {
        Budget savedBudget = Budget.builder()
                .id(1L)
                .amountLimit(new BigDecimal("5000.00"))
                .spentAmount(new BigDecimal("4000.00"))
                .month(6)
                .year(2026)
                .user(mockUser)
                .category(mockCategory)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(any(), any(), any(), any()))
                .thenReturn(Optional.empty());
        when(budgetRepository.save(any(Budget.class))).thenReturn(savedBudget);

        BudgetResponseDTO result = budgetService.createBudget(mockRequestDTO);

        assertEquals(80.0, result.getUsagePercentage(), 0.01);
        assertFalse(result.getIsOverBudget());
    }
}
