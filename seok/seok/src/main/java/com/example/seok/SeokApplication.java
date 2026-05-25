package com.example.seok;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.seok")
public class SeokApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeokApplication.class, args);
	}

}
