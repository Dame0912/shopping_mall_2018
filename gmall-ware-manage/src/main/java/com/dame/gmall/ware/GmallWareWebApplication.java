package com.dame.gmall.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.dame.gmall")
@MapperScan(basePackages = "com.dame.gmall.ware.mapper")
public class GmallWareWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallWareWebApplication.class, args);
	}
}
