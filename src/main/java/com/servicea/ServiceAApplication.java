package com.servicea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.serviceb.config.ServiceUrlConfig;

@SpringBootApplication
@EnableConfigurationProperties(ServiceUrlConfig.class)
public class ServiceAApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceAApplication.class, args);
	}

}
