package com.messagram.identity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IdentityServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(IdentityServiceApplication.class);

    public static void main(String[] args) {
        log.info("Starting Identity Service Application...");
        SpringApplication.run(IdentityServiceApplication.class, args);
    }
}
