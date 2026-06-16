package com.fintrack.service;

import com.fintrack.dto.request.GoalRequestDTO;
import com.fintrack.dto.response.GoalResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public interface GoalService {
    GoalResponseDTO createGoal(GoalRequestDTO dto);
    List<GoalResponseDTO> getGoalsByUser(Long userId);
    GoalResponseDTO getGoalById(Long id);
    GoalResponseDTO contributeToGoal(Long goalId, BigDecimal amount);
    GoalResponseDTO updateGoal(Long id, GoalRequestDTO dto);
    void deleteGoal(Long id);
}
