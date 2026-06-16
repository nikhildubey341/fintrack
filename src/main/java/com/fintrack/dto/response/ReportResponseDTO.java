package com.fintrack.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponseDTO {

    // Monthly Summary
    private Integer month;
    private Integer year;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netSavings;
    private Double savingsRate;

    // Category Breakdown
    private Map<String, BigDecimal> categoryWiseExpense;
    private Map<String, BigDecimal> categoryWiseIncome;

    // Top Expenses
    private List<CategoryExpenseDTO> topExpenses;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryExpenseDTO {
        private String categoryName;
        private BigDecimal totalAmount;
        private Double percentage;
    }
}
