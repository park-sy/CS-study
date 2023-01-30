package com.cstest.IndexTest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class IndexController {

    private final IndexService indexService;

    @GetMapping("/selectAll")
    public List<Transaction> selectAll(){
       return indexService.selectAll();
    }

    @GetMapping("/selectCategory/{category}")
    public List<Transaction> selectCategory(@PathVariable String category){
        return indexService.selectCategory(category);
    }

    @GetMapping("/selectTime")
    public List<Transaction> selectTime(){
        return indexService.selectTime();
    }

    @GetMapping("/selectCategoryAndTime/{category}")
    public List<Transaction> selectCategoryAndTime(@PathVariable String category){
        return indexService.selectCategoryAndTime(category);
    }
}
