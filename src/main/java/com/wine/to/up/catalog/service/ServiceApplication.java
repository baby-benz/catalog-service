package com.wine.to.up.catalog.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan("com.wine.to.up")
@EnableSwagger2
@EnableAsync
public class ServiceApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ServiceApplication.class, args);

    }
}
