package com.devtechi.dev.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailConfig {

    @Value("${spring.mail.username}")
    private String email;

    public String getEmail() {
        return email;
    }
}