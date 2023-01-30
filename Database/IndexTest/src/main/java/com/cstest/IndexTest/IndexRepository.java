package com.cstest.IndexTest;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface IndexRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCategory(String category);

    List<Transaction> findByDateBetween(LocalDateTime start, LocalDateTime end);

    List<Transaction> findByCategoryAndDateBetweenOrderByDate(String category, LocalDateTime start, LocalDateTime end);
}
