package com.fintrack.service.impl;

import com.fintrack.dto.request.TransactionRequestDTO;
import com.fintrack.dto.response.TransactionResponseDTO;
import com.fintrack.entity.Budget;
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
import com.fintrack.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;

    @Override
    @Transactional
    public TransactionResponseDTO addTransaction(TransactionRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", dto.getUserId()));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", dto.getCategoryId()));

        // Validate category belongs to user
        if (!category.getUser().getId().equals(dto.getUserId())) {
            throw new BadRequestException("Category does not belong to this user");
        }

        // Validate category type matches transaction type
        if (!category.getType().equals(dto.getType())) {
            throw new BadRequestException("Category type does not match transaction type");
        }

        Transaction transaction = Transaction.builder()
                .amount(dto.getAmount())
                .type(dto.getType())
                .description(dto.getDescription())
                .note(dto.getNote())
                .date(dto.getDate())
                .isRecurring(dto.getIsRecurring())
                .user(user)
                .category(category)
                .build();

        Transaction saved = transactionRepository.save(transaction);

        // Update budget spent amount if EXPENSE
        if (dto.getType() == TransactionType.EXPENSE) {
            updateBudgetSpentAmount(dto.getUserId(), dto.getCategoryId(),
                    dto.getDate().getMonthValue(), dto.getDate().getYear(), dto.getAmount().doubleValue());
        }

        log.info("Transaction added with ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    @Override
    public TransactionResponseDTO getTransactionById(Long id) {
        Transaction txn = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));
        return mapToResponse(txn);
    }

    @Override
    public Page<TransactionResponseDTO> getTransactionsByUser(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }
        return transactionRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<TransactionResponseDTO> filterTransactions(Long userId, LocalDate startDate,
            LocalDate endDate, TransactionType type, Long categoryId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }

        if (startDate != null && endDate != null) {
            return transactionRepository.findByUserIdAndDateBetween(userId, startDate, endDate, pageable)
                    .map(this::mapToResponse);
        } else if (type != null) {
            return transactionRepository.findByUserIdAndType(userId, type, pageable)
                    .map(this::mapToResponse);
        } else if (categoryId != null) {
            return transactionRepository.findByUserIdAndCategoryId(userId, categoryId, pageable)
                    .map(this::mapToResponse);
        }

        return transactionRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public TransactionResponseDTO updateTransaction(Long id, TransactionRequestDTO dto) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", dto.getCategoryId()));

        transaction.setAmount(dto.getAmount());
        transaction.setType(dto.getType());
        transaction.setDescription(dto.getDescription());
        transaction.setNote(dto.getNote());
        transaction.setDate(dto.getDate());
        transaction.setCategory(category);

        return mapToResponse(transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction", id);
        }
        transactionRepository.deleteById(id);
    }

    private void updateBudgetSpentAmount(Long userId, Long categoryId, int month, int year, double amount) {
        Optional<Budget> budgetOpt = budgetRepository
                .findByUserIdAndCategoryIdAndMonthAndYear(userId, categoryId, month, year);
        budgetOpt.ifPresent(budget -> {
            budget.setSpentAmount(budget.getSpentAmount().add(
                    java.math.BigDecimal.valueOf(amount)));
            budgetRepository.save(budget);
            log.info("Budget updated for category ID: {}", categoryId);
        });
    }

    private TransactionResponseDTO mapToResponse(Transaction t) {
        return TransactionResponseDTO.builder()
                .id(t.getId())
                .amount(t.getAmount())
                .type(t.getType())
                .description(t.getDescription())
                .note(t.getNote())
                .date(t.getDate())
                .isRecurring(t.getIsRecurring())
                .userId(t.getUser().getId())
                .userName(t.getUser().getName())
                .categoryId(t.getCategory().getId())
                .categoryName(t.getCategory().getName())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
