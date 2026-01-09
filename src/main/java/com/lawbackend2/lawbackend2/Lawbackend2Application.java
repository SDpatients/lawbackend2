package com.lawbackend2.lawbackend2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Lawbackend2Application {

    public static void main(String[] args) {
        SpringApplication.run(Lawbackend2Application.class, args);
    }

}
