package com.needbackend_app.needapp.ai.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class HuggingFaceConfig {

    @Bean
    public RestTemplate huggingFaceRestTemplate(AIProperties aiProperties) {
        return new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(aiProperties.getHuggingface().getTimeout()))
            .setReadTimeout(Duration.ofSeconds(aiProperties.getHuggingface().getTimeout()))
            .build();
    }
}