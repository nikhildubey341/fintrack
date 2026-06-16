package com.fintrack.service.impl;

import com.fintrack.dto.request.RecurringTransactionRequestDTO;
import com.fintrack.entity.Category;
import com.fintrack.entity.RecurringTransaction;
import com.fintrack.entity.User;
import com.fintrack.exception.ResourceNotFoundException;
import com.fintrack.repository.CategoryRepository;
import com.fintrack.repository.RecurringTransactionRepository;
import com.fintrack.repository.UserRepository;
import com.fintrack.service.RecurringTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecurringTransactionServiceImpl implements RecurringTransactionService {

    private final RecurringTransactionRepository recurringRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public RecurringTransaction createRecurring(RecurringTransactionRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", dto.getUserId()));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", dto.getCategoryId()));

        RecurringTransaction recurring = RecurringTransaction.builder()
                .name(dto.getName())
                .amount(dto.getAmount())
                .type(dto.getType())
                .frequency(dto.getFrequency())
                .nextExecutionDate(dto.getStartDate())
                .description(dto.getDescription())
                .isActive(true)
                .user(user)
                .category(category)
                .build();

        RecurringTransaction saved = recurringRepository.save(recurring);
        log.info("Recurring transaction created with ID: {}", saved.getId());
        return saved;
    }

    @Override
    public List<RecurringTransaction> getByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }
        return recurringRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        RecurringTransaction rt = recurringRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RecurringTransaction", id));
        rt.setIsActive(false);
        recurringRepository.save(rt);
        log.info("Recurring transaction deactivated: {}", id);
    }
}
