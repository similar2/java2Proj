package org.example.java2final;

import org.example.java2final.config.Result;
import org.example.java2final.service.ExceptionService;
import org.example.java2final.service.TopicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
class Java2FinalApplicationTests {
    @Autowired
    private TopicService topicService;
    @Autowired
    private ExceptionService exceptionService;

    @Test
    void contextLoads() {
    }

    @Test
    void getTopic() {
        System.out.println(topicService.getTopic(-1L).toString());
        System.out.println("------------------");
        System.out.println(topicService.getTopic("spring").getData().toString());
    }

    @Test
    void getExceptions() {
        Result<List<Map<String, Integer>>> exceptions = exceptionService.getTopNExceptions(0);
        System.out.println(exceptions.toString());
        Result<Map<String, Integer>> exception = exceptionService.getExceptionFrequency("invalidone");
        System.out.println(exception.toString());
    }

}
