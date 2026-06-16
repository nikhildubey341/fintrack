package com.fintrack.service;

import com.fintrack.dto.request.TransactionRequestDTO;
import com.fintrack.dto.response.TransactionResponseDTO;
import com.fintrack.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface TransactionService {
    TransactionResponseDTO addTransaction(TransactionRequestDTO dto);
    TransactionResponseDTO getTransactionById(Long id);
    Page<TransactionResponseDTO> getTransactionsByUser(Long userId, Pageable pageable);
    Page<TransactionResponseDTO> filterTransactions(Long userId, LocalDate startDate, LocalDate endDate,
                                                     TransactionType type, Long categoryId, Pageable pageable);
    TransactionResponseDTO updateTransaction(Long id, TransactionRequestDTO dto);
    void deleteTransaction(Long id);
}
