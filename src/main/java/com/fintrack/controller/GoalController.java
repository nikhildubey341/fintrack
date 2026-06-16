package com.fintrack.controller;

import com.fintrack.dto.request.GoalRequestDTO;
import com.fintrack.dto.response.ApiResponse;
import com.fintrack.dto.response.GoalResponseDTO;
import com.fintrack.service.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Tag(name = "Goal Management", description = "APIs for managing financial goals")
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    @Operation(summary = "Create a new financial goal")
    public ResponseEntity<ApiResponse<GoalResponseDTO>> createGoal(
            @Valid @RequestBody GoalRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Goal created", goalService.createGoal(dto)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all goals for a user")
    public ResponseEntity<ApiResponse<List<GoalResponseDTO>>> getGoalsByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Goals fetched", goalService.getGoalsByUser(userId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get goal by ID")
    public ResponseEntity<ApiResponse<GoalResponseDTO>> getGoalById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Goal fetched", goalService.getGoalById(id)));
    }

    @PatchMapping("/{id}/contribute")
    @Operation(summary = "Add money to a goal")
    public ResponseEntity<ApiResponse<GoalResponseDTO>> contributeToGoal(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(ApiResponse.success("Contribution added",
                goalService.contributeToGoal(id, amount)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update goal details")
    public ResponseEntity<ApiResponse<GoalResponseDTO>> updateGoal(
            @PathVariable Long id, @Valid @RequestBody GoalRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Goal updated", goalService.updateGoal(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete goal")
    public ResponseEntity<ApiResponse<Void>> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.ok(ApiResponse.success("Goal deleted"));
    }
}
