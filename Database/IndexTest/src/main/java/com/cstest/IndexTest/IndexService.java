package com.cstest.IndexTest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IndexService {

    private final IndexRepository indexRepository;
    public List<Transaction> selectAll() {
        return indexRepository.findAll();
    }

    public List<Transaction> selectCategory(String category) {
        return indexRepository.findByCategory(category);
    }

    public List<Transaction> selectTime() {
        LocalDateTime start = LocalDateTime.of(2022,1,1,0,0);
        LocalDateTime end = LocalDateTime.of(2022,1,31,23,59);
        return indexRepository.findByDateBetween(start, end);
    }

    public List<Transaction> selectCategoryAndTime(String category) {
        LocalDateTime start = LocalDateTime.of(2022,1,1,0,0);
        LocalDateTime end = LocalDateTime.of(2022,1,31,23,59);
        return indexRepository.findByCategoryAndDateBetweenOrderByDate(category,start, end);
    }
}
