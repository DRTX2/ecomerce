package com.drtx.ecomerce.amazon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.drtx.ecomerce.amazon.core",
		"com.drtx.ecomerce.amazon.application",
		"com.drtx.ecomerce.amazon.adapters",
		"com.drtx.ecomerce.amazon.infrastructure"
})
@EnableJpaRepositories(basePackages = {
		"com.drtx.ecomerce.amazon.adapters.out.persistence",
		"com.drtx.ecomerce.amazon.adapters.in.security"
})
@EntityScan(basePackages = {
		"com.drtx.ecomerce.amazon.adapters.out.persistence.entity",
		"com.drtx.ecomerce.amazon.adapters.in.security"
})
public class AmazonApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmazonApplication.class, args);
	}

}
