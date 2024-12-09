package com.example.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "scrap")
public class ScrapProperties {
    private String baseUrl;
    private String endPoint;
    private String apiKey;
}
