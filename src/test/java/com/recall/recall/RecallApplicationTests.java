package com.recall.recall;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;


@SpringBootTest
class RecallApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
		assertNotNull(context, "Application context should load successfully");
		assertTrue(context.containsBean("customerController"), "CustomerController bean should be present in the context");
    }
}
