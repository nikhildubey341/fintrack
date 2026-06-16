package com.fintrack.repository;

import com.fintrack.entity.Category;
import com.fintrack.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserId(Long userId);

    List<Category> findByUserIdAndType(Long userId, TransactionType type);

    boolean existsByNameAndUserId(String name, Long userId);
}
