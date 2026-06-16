package com.fintrack.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetResponseDTO {
    private Long id;
    private BigDecimal amountLimit;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private Double usagePercentage;
    private Integer month;
    private Integer year;
    private Long userId;
    private Long categoryId;
    private String categoryName;
    private Boolean isOverBudget;
}
