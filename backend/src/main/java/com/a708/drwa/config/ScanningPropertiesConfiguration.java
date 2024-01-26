package com.a708.drwa.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

/**
 * @ConfigurationPropertiesScan
 * - @ConfigurationProperties를 사용하는 클래스를 찾아서 Bean으로 등록한다.
 * - @ConfigurationProperties를 사용하는 클래스는 @Component를 사용하지 않아도 된다.
 */
@Configuration
@ConfigurationPropertiesScan(basePackages = "com.a708.drwa")
public class ScanningPropertiesConfiguration {
}
