package com.taskreminder.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaskappApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskappApplication.class, args);
    }
}
