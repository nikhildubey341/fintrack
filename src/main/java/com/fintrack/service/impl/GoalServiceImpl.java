package com.fintrack.service.impl;

import com.fintrack.dto.request.GoalRequestDTO;
import com.fintrack.dto.response.GoalResponseDTO;
import com.fintrack.entity.Goal;
import com.fintrack.entity.User;
import com.fintrack.enums.GoalStatus;
import com.fintrack.exception.BadRequestException;
import com.fintrack.exception.ResourceNotFoundException;
import com.fintrack.repository.GoalRepository;
import com.fintrack.repository.UserRepository;
import com.fintrack.service.GoalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public GoalResponseDTO createGoal(GoalRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", dto.getUserId()));

        Goal goal = Goal.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .targetAmount(dto.getTargetAmount())
                .savedAmount(BigDecimal.ZERO)
                .deadline(dto.getDeadline())
                .status(GoalStatus.IN_PROGRESS)
                .user(user)
                .build();

        return mapToResponse(goalRepository.save(goal));
    }

    @Override
    public List<GoalResponseDTO> getGoalsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }
        return goalRepository.findByUserId(userId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public GoalResponseDTO getGoalById(Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", id));
        return mapToResponse(goal);
    }

    @Override
    @Transactional
    public GoalResponseDTO contributeToGoal(Long goalId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Contribution amount must be positive");
        }

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", goalId));

        if (goal.getStatus() == GoalStatus.COMPLETED) {
            throw new BadRequestException("Goal is already completed");
        }

        goal.setSavedAmount(goal.getSavedAmount().add(amount));

        // Auto-complete goal if target reached
        if (goal.getSavedAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
            log.info("Goal '{}' completed!", goal.getName());
        }

        return mapToResponse(goalRepository.save(goal));
    }

    @Override
    @Transactional
    public GoalResponseDTO updateGoal(Long id, GoalRequestDTO dto) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", id));

        goal.setName(dto.getName());
        goal.setDescription(dto.getDescription());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setDeadline(dto.getDeadline());

        return mapToResponse(goalRepository.save(goal));
    }

    @Override
    @Transactional
    public void deleteGoal(Long id) {
        if (!goalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Goal", id);
        }
        goalRepository.deleteById(id);
    }

    private GoalResponseDTO mapToResponse(Goal g) {
        BigDecimal remaining = g.getTargetAmount().subtract(g.getSavedAmount());
        double progress = 0.0;
        if (g.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            progress = g.getSavedAmount()
                    .divide(g.getTargetAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), g.getDeadline());

        return GoalResponseDTO.builder()
                .id(g.getId())
                .name(g.getName())
                .description(g.getDescription())
                .targetAmount(g.getTargetAmount())
                .savedAmount(g.getSavedAmount())
                .remainingAmount(remaining.max(BigDecimal.ZERO))
                .progressPercentage(Math.min(progress, 100.0))
                .deadline(g.getDeadline())
                .status(g.getStatus())
                .userId(g.getUser().getId())
                .daysRemaining(daysRemaining)
                .build();
    }
}
