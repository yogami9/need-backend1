package com.needbackend_app.needapp.auth.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class RSAKeyLoadingTest {

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Test
    public void testRSAKeysLoadCorrectly() {
        // If the keys don't load correctly, the application context won't start
        // and this test will fail before reaching these assertions
        assertNotNull(jwtEncoder, "JwtEncoder should be initialized");
        assertNotNull(jwtDecoder, "JwtDecoder should be initialized");
        
        // The test passes if we can successfully autowire the encoder and decoder
        // which means the RSA keys were loaded correctly
    }
}