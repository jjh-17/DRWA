package com.a708.drwa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@EnableScheduling
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class DrwaApplication {
	static {
		System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
	}
	public static void main(String[] args) {
		SpringApplication.run(DrwaApplication.class, args);
	}

}
