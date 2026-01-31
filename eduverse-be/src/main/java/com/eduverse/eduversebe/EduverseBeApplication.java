package com.eduverse.eduversebe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EduverseBeApplication {
    public static void main(String[] args) {
        SpringApplication.run(EduverseBeApplication.class, args);
    }
}
