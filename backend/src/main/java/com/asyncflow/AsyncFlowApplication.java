package com.asyncflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableR2dbcAuditing
@EnableKafka
@EnableScheduling
public class AsyncFlowApplication {
    public static void main(String[] args) {
        SpringApplication.run(AsyncFlowApplication.class, args);
    }
}
