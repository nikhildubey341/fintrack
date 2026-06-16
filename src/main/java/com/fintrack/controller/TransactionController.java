package com.fintrack.controller;

import com.fintrack.dto.request.TransactionRequestDTO;
import com.fintrack.dto.response.ApiResponse;
import com.fintrack.dto.response.TransactionResponseDTO;
import com.fintrack.enums.TransactionType;
import com.fintrack.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction Management", description = "APIs for managing financial transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Add a new transaction")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> addTransaction(
            @Valid @RequestBody TransactionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transaction added", transactionService.addTransaction(dto)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Transaction fetched", transactionService.getTransactionById(id)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all transactions for a user with pagination")
    public ResponseEntity<ApiResponse<Page<TransactionResponseDTO>>> getTransactionsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TransactionResponseDTO> result = transactionService.getTransactionsByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Transactions fetched", result));
    }

    @GetMapping("/user/{userId}/filter")
    @Operation(summary = "Filter transactions by date range, type, or category")
    public ResponseEntity<ApiResponse<Page<TransactionResponseDTO>>> filterTransactions(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<TransactionResponseDTO> result = transactionService.filterTransactions(
                userId, startDate, endDate, type, categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Filtered transactions", result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update transaction")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> updateTransaction(
            @PathVariable Long id, @Valid @RequestBody TransactionRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Transaction updated",
                transactionService.updateTransaction(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete transaction")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok(ApiResponse.success("Transaction deleted"));
    }
}
