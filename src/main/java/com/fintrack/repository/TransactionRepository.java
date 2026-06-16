package com.fintrack.repository;

import com.fintrack.entity.Transaction;
import com.fintrack.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByUserId(Long userId, Pageable pageable);

    // Filter by date range
    Page<Transaction> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    // Filter by type
    Page<Transaction> findByUserIdAndType(Long userId, TransactionType type, Pageable pageable);

    // Filter by category
    Page<Transaction> findByUserIdAndCategoryId(Long userId, Long categoryId, Pageable pageable);

    // Total income/expense for a user in a month
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = :type " +
           "AND MONTH(t.date) = :month AND YEAR(t.date) = :year")
    BigDecimal sumByUserIdAndTypeAndMonthAndYear(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("month") int month,
            @Param("year") int year);

    // Category-wise expense breakdown for a month
    @Query("SELECT t.category.name, COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = 'EXPENSE' " +
           "AND MONTH(t.date) = :month AND YEAR(t.date) = :year " +
           "GROUP BY t.category.name ORDER BY SUM(t.amount) DESC")
    List<Object[]> getCategoryWiseExpense(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year);

    // Category-wise income breakdown for a month
    @Query("SELECT t.category.name, COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = 'INCOME' " +
           "AND MONTH(t.date) = :month AND YEAR(t.date) = :year " +
           "GROUP BY t.category.name ORDER BY SUM(t.amount) DESC")
    List<Object[]> getCategoryWiseIncome(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year);

    // Total spending for a category in a specific month
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.category.id = :categoryId " +
           "AND t.type = 'EXPENSE' " +
           "AND MONTH(t.date) = :month AND YEAR(t.date) = :year")
    BigDecimal sumExpenseByCategoryAndMonth(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("month") int month,
            @Param("year") int year);

    // Yearly summary month-wise
    @Query("SELECT MONTH(t.date), t.type, COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND YEAR(t.date) = :year " +
           "GROUP BY MONTH(t.date), t.type ORDER BY MONTH(t.date)")
    List<Object[]> getYearlySummary(
            @Param("userId") Long userId,
            @Param("year") int year);

    // Top 5 expense categories in a month
    @Query("SELECT t.category.name, COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.type = 'EXPENSE' " +
           "AND MONTH(t.date) = :month AND YEAR(t.date) = :year " +
           "GROUP BY t.category.name ORDER BY SUM(t.amount) DESC")
    List<Object[]> getTopExpenseCategories(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year,
            Pageable pageable);
}
