package com.a708.drwa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class DrwaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DrwaApplication.class, args);
	}

}
