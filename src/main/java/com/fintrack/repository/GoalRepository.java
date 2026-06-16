package com.fintrack.repository;

import com.fintrack.entity.Goal;
import com.fintrack.enums.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByUserId(Long userId);

    List<Goal> findByUserIdAndStatus(Long userId, GoalStatus status);

    List<Goal> findByDeadlineBeforeAndStatus(LocalDate date, GoalStatus status);
}
