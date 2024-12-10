package org.example.java2final;

import org.example.java2final.util.DataScrapeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ScrapeTest {

    @Autowired
    private DataScrapeUtil dataScrapeUtil; // Automatically inject the DataScrapeUtil component

    @Test
    public void test() {
        dataScrapeUtil.dataScrape();
//        dataScrapeUtil.scrapeQuestion(5, 0); // Call the scrapeQuestion method
//        dataScrapeUtil.scrapeUserDetail("14268583",5,0);
//        dataScrapeUtil.scrapeAnswers("79265831", 5, 0);
    }
}
