package com.shopx.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.shopx")
public class ShopJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopJobApplication.class, args);
    }
}
