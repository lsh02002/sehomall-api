package com.example.sehomallapi;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;

@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
public class SehomallApiApplication {

    public static void main(String[] args)
    {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        SpringApplication.run(SehomallApiApplication.class, args);
    }
}

