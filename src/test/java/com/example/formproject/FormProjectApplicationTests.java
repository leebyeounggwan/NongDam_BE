package com.example.formproject;

import com.example.formproject.repository.AccountBookRepository;
import com.example.formproject.repository.WorkLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;

@SpringBootTest
class FormProjectApplicationTests {
    @Autowired
    WorkLogRepository workLogRepository;
    @Test
    void contextLoads() {
//        List<Object[]> obj = workLogRepository.selectHarvest(0);
//        List<Object[]> obj2 = repository.spandOfYear(0);
    }

}
