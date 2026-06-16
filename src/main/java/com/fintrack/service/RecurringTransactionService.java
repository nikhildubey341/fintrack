package com.fintrack.service;

import com.fintrack.dto.request.RecurringTransactionRequestDTO;
import com.fintrack.dto.response.ApiResponse;
import com.fintrack.entity.RecurringTransaction;

import java.util.List;

public interface RecurringTransactionService {
    RecurringTransaction createRecurring(RecurringTransactionRequestDTO dto);
    List<RecurringTransaction> getByUser(Long userId);
    void deactivate(Long id);
}
