package com.fintrack.repository;

import com.fintrack.entity.RecurringTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {

    List<RecurringTransaction> findByIsActiveTrue();

    List<RecurringTransaction> findByNextExecutionDateLessThanEqualAndIsActiveTrue(LocalDate date);

    List<RecurringTransaction> findByUserId(Long userId);
}
