package com.buildsage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class BuildSageApplication {
    public static void main(String[] args) {
        SpringApplication.run(BuildSageApplication.class, args);
    }
}
