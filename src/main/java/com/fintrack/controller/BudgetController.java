package com.fintrack.controller;

import com.fintrack.dto.request.BudgetRequestDTO;
import com.fintrack.dto.response.ApiResponse;
import com.fintrack.dto.response.BudgetResponseDTO;
import com.fintrack.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Tag(name = "Budget Management", description = "APIs for managing monthly budgets")
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    @Operation(summary = "Create a monthly budget for a category")
    public ResponseEntity<ApiResponse<BudgetResponseDTO>> createBudget(
            @Valid @RequestBody BudgetRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Budget created", budgetService.createBudget(dto)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all budgets for a user")
    public ResponseEntity<ApiResponse<List<BudgetResponseDTO>>> getBudgetsByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Budgets fetched", budgetService.getBudgetsByUser(userId)));
    }

    @GetMapping("/user/{userId}/month")
    @Operation(summary = "Get budgets for a specific month and year")
    public ResponseEntity<ApiResponse<List<BudgetResponseDTO>>> getBudgetsByMonth(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().monthValue}") int month,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().year}") int year) {
        return ResponseEntity.ok(ApiResponse.success("Monthly budgets fetched",
                budgetService.getBudgetsByUserAndMonth(userId, month, year)));
    }

    @GetMapping("/user/{userId}/alerts/overspent")
    @Operation(summary = "Get overspending alerts for current month")
    public ResponseEntity<ApiResponse<List<BudgetResponseDTO>>> getOverspendingAlerts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int month,
            @RequestParam(defaultValue = "0") int year) {
        int m = month == 0 ? LocalDate.now().getMonthValue() : month;
        int y = year == 0 ? LocalDate.now().getYear() : year;
        return ResponseEntity.ok(ApiResponse.success("Overspending alerts",
                budgetService.getOverspendingAlerts(userId, m, y)));
    }

    @GetMapping("/user/{userId}/alerts/near-limit")
    @Operation(summary = "Get near-limit budget alerts (>80% spent)")
    public ResponseEntity<ApiResponse<List<BudgetResponseDTO>>> getNearLimitAlerts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int month,
            @RequestParam(defaultValue = "0") int year) {
        int m = month == 0 ? LocalDate.now().getMonthValue() : month;
        int y = year == 0 ? LocalDate.now().getYear() : year;
        return ResponseEntity.ok(ApiResponse.success("Near-limit alerts",
                budgetService.getNearLimitAlerts(userId, m, y)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update budget limit")
    public ResponseEntity<ApiResponse<BudgetResponseDTO>> updateBudget(
            @PathVariable Long id, @Valid @RequestBody BudgetRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Budget updated", budgetService.updateBudget(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete budget")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.ok(ApiResponse.success("Budget deleted"));
    }
}
