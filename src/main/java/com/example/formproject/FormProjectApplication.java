package com.example.formproject;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableAdminServer
@EnableCaching
public class FormProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(FormProjectApplication.class, args);
    }

}
