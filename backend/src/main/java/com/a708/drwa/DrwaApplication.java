package com.a708.drwa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DrwaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DrwaApplication.class, args);
	}

}
