package com.fintrack.repository;

import com.fintrack.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserId(Long userId);

    List<Budget> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);

    Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYear(
            Long userId, Long categoryId, Integer month, Integer year);

    // Budgets where spending exceeds limit (overspending alerts)
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
           "AND b.month = :month AND b.year = :year " +
           "AND b.spentAmount >= b.amountLimit")
    List<Budget> findOverspentBudgets(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year);

    // Budgets where spending > 80% of limit (warning alerts)
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
           "AND b.month = :month AND b.year = :year " +
           "AND (b.spentAmount / b.amountLimit) >= 0.8")
    List<Budget> findNearLimitBudgets(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year);
}
