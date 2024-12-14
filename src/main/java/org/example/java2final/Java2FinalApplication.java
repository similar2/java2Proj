package org.example.java2final;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class Java2FinalApplication {

    public static void main(String[] args) {
        SpringApplication.run(Java2FinalApplication.class, args);
    }

}
