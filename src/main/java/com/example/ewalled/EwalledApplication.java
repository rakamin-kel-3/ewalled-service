package com.example.ewalled;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EwalledApplication {

	public static void main(String[] args) {
		SpringApplication.run(EwalledApplication.class, args);
	}

}
