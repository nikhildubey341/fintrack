package com.fintrack.dto.response;

import com.fintrack.enums.GoalStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal targetAmount;
    private BigDecimal savedAmount;
    private BigDecimal remainingAmount;
    private Double progressPercentage;
    private LocalDate deadline;
    private GoalStatus status;
    private Long userId;
    private Long daysRemaining;
}
