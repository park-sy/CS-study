package com.cstest.IndexTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import com.cstest.IndexTest.Transaction;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.toIntExact;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class IndexTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IndexRepository indexRepository;
    String[] category = {"패션","뷰티","출산","식품","주방용품","생활용품","디지털"};
    long stime;

    @BeforeEach
    void checkTime(){
        stime = System.currentTimeMillis();
    }
//    @Test
//    @DisplayName("데이터 삽입")
//    void createData() throws Exception{
//
//        Random random = new Random();
//        LocalDateTime stand = LocalDateTime.of(2022,1,1,1,1,1);
//        List<Transaction> transactions = IntStream.range(0,100000)
//                .mapToObj(i->Transaction.builder()
//                        .category(category[random.nextInt(category.length)])
//                        .date(stand.plusSeconds(random.nextInt(31536000)))
//                        .amount((random.nextInt(100000)+1)*10).build()
//                ).collect(Collectors.toList());
//        indexRepository.saveAll(transactions);
//        System.out.println("데이터 삽입 소요시간:"+(System.currentTimeMillis()-stime)+"ms");
//    }

    @Test
    @DisplayName("전체 조회")
    void getAll() throws Exception{
        mockMvc.perform(get("/selectAll").contentType(APPLICATION_JSON));
        System.out.println("전체 조회 소요시간:"+(System.currentTimeMillis()-stime)+"ms");
    }
    @Test
    @DisplayName("특정 카테고리 조회")
    void getCategory() throws Exception{

        mockMvc.perform(get("/selectCategory/{category}",category[0]).contentType(APPLICATION_JSON));
        System.out.println("특정 카테고리 조회 소요시간:"+(System.currentTimeMillis()-stime)+"ms");
    }
    @Test
    @DisplayName("시간 범위 조회")
    void getJanuary() throws Exception{
        mockMvc.perform(get("/selectTime").contentType(APPLICATION_JSON));
        System.out.println("시간 범위 조회 소요시간:"+(System.currentTimeMillis()-stime)+"ms");
    }
    @Test
    @DisplayName("특정 카테고리 & 시간 범위조회")
    void getCategoryAndJanuary() throws Exception{
        mockMvc.perform(get("/selectCategoryAndTime/{category}",category[0]).contentType(APPLICATION_JSON));
        System.out.println("특정 카테고리 & 시간 범위조회 소요시간:"+(System.currentTimeMillis()-stime)+"ms");
    }
}
