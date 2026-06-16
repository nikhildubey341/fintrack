package com.fintrack.service;

import com.fintrack.dto.request.TransactionRequestDTO;
import com.fintrack.dto.response.TransactionResponseDTO;
import com.fintrack.entity.Category;
import com.fintrack.entity.Transaction;
import com.fintrack.entity.User;
import com.fintrack.enums.TransactionType;
import com.fintrack.exception.BadRequestException;
import com.fintrack.exception.ResourceNotFoundException;
import com.fintrack.repository.BudgetRepository;
import com.fintrack.repository.CategoryRepository;
import com.fintrack.repository.TransactionRepository;
import com.fintrack.repository.UserRepository;
import com.fintrack.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Transaction Service Tests")
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private UserRepository userRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private BudgetRepository budgetRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User mockUser;
    private Category mockCategory;
    private TransactionRequestDTO mockRequestDTO;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .name("Nikhil")
                .email("nikhil@test.com")
                .phone("9876543210")
                .build();

        mockCategory = Category.builder()
                .id(1L)
                .name("Food")
                .type(TransactionType.EXPENSE)
                .user(mockUser)
                .build();

        mockRequestDTO = TransactionRequestDTO.builder()
                .amount(new BigDecimal("500.00"))
                .type(TransactionType.EXPENSE)
                .description("Dinner at restaurant")
                .date(LocalDate.now())
                .isRecurring(false)
                .userId(1L)
                .categoryId(1L)
                .build();
    }

    @Test
    @DisplayName("Should add transaction successfully")
    void shouldAddTransactionSuccessfully() {
        // Arrange
        Transaction savedTransaction = Transaction.builder()
                .id(1L)
                .amount(mockRequestDTO.getAmount())
                .type(mockRequestDTO.getType())
                .description(mockRequestDTO.getDescription())
                .date(mockRequestDTO.getDate())
                .isRecurring(false)
                .user(mockUser)
                .category(mockCategory)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(any(), any(), any(), any()))
                .thenReturn(Optional.empty());

        // Act
        TransactionResponseDTO result = transactionService.addTransaction(mockRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("500.00"), result.getAmount());
        assertEquals(TransactionType.EXPENSE, result.getType());
        assertEquals("Dinner at restaurant", result.getDescription());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        mockRequestDTO.setUserId(99L);

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.addTransaction(mockRequestDTO));
    }

    @Test
    @DisplayName("Should throw BadRequestException when category type mismatch")
    void shouldThrowExceptionWhenCategoryTypeMismatch() {
        // Category is INCOME but transaction is EXPENSE
        mockCategory.setType(TransactionType.INCOME);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));

        assertThrows(BadRequestException.class,
                () -> transactionService.addTransaction(mockRequestDTO));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when transaction not found")
    void shouldThrowExceptionWhenTransactionNotFound() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.getTransactionById(99L));
    }
}
