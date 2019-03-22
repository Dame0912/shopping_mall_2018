package com.dame.gmall.list;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.dame.gmall"})
@SpringBootApplication
public class GmallListServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallListServiceApplication.class, args);
    }

}
