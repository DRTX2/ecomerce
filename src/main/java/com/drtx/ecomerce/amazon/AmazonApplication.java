package com.drtx.ecomerce.amazon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.drtx.ecomerce.amazon.core",
		"com.drtx.ecomerce.amazon.application",
		"com.drtx.ecomerce.amazon.adapters",
		"com.drtx.ecomerce.amazon.infrastructure"
})
public class AmazonApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmazonApplication.class, args);
	}

}
