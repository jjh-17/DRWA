package com.a708.drwa.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan(basePackages = "com.a708.drwa")
public class ScanningPropertiesConfiguration {
}
