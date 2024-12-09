package com.example.demo.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class EncryptionConfig {

    @Value("${encryption.secret-key}")
    private String secretKey;

}
