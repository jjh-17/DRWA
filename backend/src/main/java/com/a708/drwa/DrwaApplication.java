package com.a708.drwa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableCaching
public class DrwaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DrwaApplication.class, args);
	}

}
