package com.drtx.ecomerce.amazon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AmazonApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmazonApplication.class, args);
	}

}
