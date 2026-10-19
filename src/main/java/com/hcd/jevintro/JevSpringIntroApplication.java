package com.hcd.jevintro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class JevSpringIntroApplication {

    static void main(String[] args) {
        SpringApplication.run(JevSpringIntroApplication.class, args);
    }
}
