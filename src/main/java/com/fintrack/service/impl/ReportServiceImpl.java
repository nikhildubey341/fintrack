package com.fintrack.service.impl;

import com.fintrack.dto.response.ReportResponseDTO;
import com.fintrack.enums.TransactionType;
import com.fintrack.exception.ResourceNotFoundException;
import com.fintrack.repository.TransactionRepository;
import com.fintrack.repository.UserRepository;
import com.fintrack.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Override
    public ReportResponseDTO getMonthlySummary(Long userId, int month, int year) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }

        BigDecimal totalIncome = transactionRepository
                .sumByUserIdAndTypeAndMonthAndYear(userId, TransactionType.INCOME, month, year);
        BigDecimal totalExpense = transactionRepository
                .sumByUserIdAndTypeAndMonthAndYear(userId, TransactionType.EXPENSE, month, year);

        BigDecimal netSavings = totalIncome.subtract(totalExpense);

        double savingsRate = 0.0;
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            savingsRate = netSavings
                    .divide(totalIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        // Category-wise breakdown
        Map<String, BigDecimal> expenseBreakdown = new LinkedHashMap<>();
        List<Object[]> expenseRows = transactionRepository.getCategoryWiseExpense(userId, month, year);
        for (Object[] row : expenseRows) {
            expenseBreakdown.put((String) row[0], (BigDecimal) row[1]);
        }

        Map<String, BigDecimal> incomeBreakdown = new LinkedHashMap<>();
        List<Object[]> incomeRows = transactionRepository.getCategoryWiseIncome(userId, month, year);
        for (Object[] row : incomeRows) {
            incomeBreakdown.put((String) row[0], (BigDecimal) row[1]);
        }

        // Top 5 expenses
        List<Object[]> topExpRows = transactionRepository
                .getTopExpenseCategories(userId, month, year, PageRequest.of(0, 5));
        List<ReportResponseDTO.CategoryExpenseDTO> topExpenses = new ArrayList<>();
        for (Object[] row : topExpRows) {
            double pct = totalExpense.compareTo(BigDecimal.ZERO) > 0
                    ? ((BigDecimal) row[1]).divide(totalExpense, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)).doubleValue()
                    : 0.0;
            topExpenses.add(ReportResponseDTO.CategoryExpenseDTO.builder()
                    .categoryName((String) row[0])
                    .totalAmount((BigDecimal) row[1])
                    .percentage(pct)
                    .build());
        }

        return ReportResponseDTO.builder()
                .month(month)
                .year(year)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netSavings(netSavings)
                .savingsRate(savingsRate)
                .categoryWiseExpense(expenseBreakdown)
                .categoryWiseIncome(incomeBreakdown)
                .topExpenses(topExpenses)
                .build();
    }

    @Override
    public ReportResponseDTO getYearlySummary(Long userId, int year) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (int m = 1; m <= 12; m++) {
            totalIncome = totalIncome.add(
                    transactionRepository.sumByUserIdAndTypeAndMonthAndYear(userId, TransactionType.INCOME, m, year));
            totalExpense = totalExpense.add(
                    transactionRepository.sumByUserIdAndTypeAndMonthAndYear(userId, TransactionType.EXPENSE, m, year));
        }

        BigDecimal netSavings = totalIncome.subtract(totalExpense);
        double savingsRate = 0.0;
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            savingsRate = netSavings
                    .divide(totalIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        return ReportResponseDTO.builder()
                .year(year)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netSavings(netSavings)
                .savingsRate(savingsRate)
                .build();
    }
}
