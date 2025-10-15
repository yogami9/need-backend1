package com.needbackend_app.needapp.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;

@Configuration
public class RSAKeyConfig {

    @Value("classpath:certs/publicKey.pem")
    private Resource publicKeyResource;

    @Value("classpath:certs/privateKey.pem")
    private Resource privateKeyResource;

    @Bean
    public RSAPublicKey rsaPublicKey() throws Exception {
        String key = readKeyFromFile(publicKeyResource.getInputStream(),
                "-----BEGIN PUBLIC KEY-----", "-----END PUBLIC KEY-----");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
    }

    @Bean
    public RSAPrivateKey rsaPrivateKey() throws Exception {
        String key = readKeyFromFile(privateKeyResource.getInputStream(),
                "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    private String readKeyFromFile(InputStream inputStream, String beginMarker, String endMarker) throws IOException {
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .filter(line -> !line.contains(beginMarker) && !line.contains(endMarker))
                .collect(Collectors.joining());
    }

    @Bean
    public ConfigProperties configProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        return new ConfigProperties(new ConfigProperties.RSAKeys(publicKey, privateKey));
    }
}

