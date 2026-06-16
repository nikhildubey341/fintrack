package com.fintrack.service;

import com.fintrack.dto.request.BudgetRequestDTO;
import com.fintrack.dto.response.BudgetResponseDTO;

import java.util.List;

public interface BudgetService {
    BudgetResponseDTO createBudget(BudgetRequestDTO dto);
    List<BudgetResponseDTO> getBudgetsByUser(Long userId);
    List<BudgetResponseDTO> getBudgetsByUserAndMonth(Long userId, int month, int year);
    BudgetResponseDTO updateBudget(Long id, BudgetRequestDTO dto);
    List<BudgetResponseDTO> getOverspendingAlerts(Long userId, int month, int year);
    List<BudgetResponseDTO> getNearLimitAlerts(Long userId, int month, int year);
    void deleteBudget(Long id);
}
