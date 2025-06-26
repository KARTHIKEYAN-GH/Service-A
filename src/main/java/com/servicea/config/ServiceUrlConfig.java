package com.servicea.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;


import lombok.Data;

@Service
@ConfigurationProperties(prefix = "microservices")
@Data
public class ServiceUrlConfig {
    private String serviceA;
    private String serviceB;
}