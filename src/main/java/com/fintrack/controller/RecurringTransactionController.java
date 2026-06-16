package com.fintrack.controller;

import com.fintrack.dto.request.RecurringTransactionRequestDTO;
import com.fintrack.dto.response.ApiResponse;
import com.fintrack.entity.RecurringTransaction;
import com.fintrack.service.RecurringTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recurring-transactions")
@RequiredArgsConstructor
@Tag(name = "Recurring Transactions", description = "APIs for managing recurring transactions (SIP, EMI, Salary etc.)")
public class RecurringTransactionController {

    private final RecurringTransactionService recurringTransactionService;

    @PostMapping
    @Operation(summary = "Create a new recurring transaction")
    public ResponseEntity<ApiResponse<RecurringTransaction>> createRecurring(
            @Valid @RequestBody RecurringTransactionRequestDTO dto) {
        RecurringTransaction saved = recurringTransactionService.createRecurring(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Recurring transaction created", saved));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all recurring transactions for a user")
    public ResponseEntity<ApiResponse<List<RecurringTransaction>>> getByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Recurring transactions fetched",
                recurringTransactionService.getByUser(userId)));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a recurring transaction")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        recurringTransactionService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.success("Recurring transaction deactivated"));
    }
}
