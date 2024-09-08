package com.example.sehomallapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SehomallApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SehomallApiApplication.class, args);
    }

}
