package com.needbackend_app.needapp.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "app.security")
public record ConfigProperties(RSAKeys rsaKeys) {
    public record RSAKeys(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
    }
}
