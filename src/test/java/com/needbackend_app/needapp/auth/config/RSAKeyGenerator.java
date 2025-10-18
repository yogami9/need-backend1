package com.needbackend_app.needapp.auth.config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

public class RSAKeyGenerator {
    public static void main(String[] args) {
        try {
            // Generate RSA key pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            
            // Create directory if it doesn't exist
            Path certsDir = Paths.get("src/main/resources/certs");
            if (!Files.exists(certsDir)) {
                Files.createDirectories(certsDir);
            }
            
            // Write public key to file
            String publicKeyPEM = "-----BEGIN PUBLIC KEY-----\n" +
                    Base64.getEncoder().encodeToString(publicKey.getEncoded())
                            .replaceAll("(.{64})", "$1\n") +
                    "\n-----END PUBLIC KEY-----\n";
            
            // Write private key to file
            String privateKeyPEM = "-----BEGIN PRIVATE KEY-----\n" +
                    Base64.getEncoder().encodeToString(privateKey.getEncoded())
                            .replaceAll("(.{64})", "$1\n") +
                    "\n-----END PRIVATE KEY-----\n";
            
            // Ensure Unix-style line endings (LF only)
            publicKeyPEM = publicKeyPEM.replace("\r\n", "\n");
            privateKeyPEM = privateKeyPEM.replace("\r\n", "\n");
            
            // Write to files
            try (FileOutputStream publicKeyOS = new FileOutputStream("src/main/resources/certs/publicKey.pem")) {
                publicKeyOS.write(publicKeyPEM.getBytes());
            }
            
            try (FileOutputStream privateKeyOS = new FileOutputStream("src/main/resources/certs/privateKey.pem")) {
                privateKeyOS.write(privateKeyPEM.getBytes());
            }
            
            System.out.println("RSA keys generated successfully!");
            System.out.println("Public key saved to: src/main/resources/certs/publicKey.pem");
            System.out.println("Private key saved to: src/main/resources/certs/privateKey.pem");
            
            // Verify the keys can be loaded
            System.out.println("\nVerifying keys can be loaded...");
            String publicKeyContent = Files.readString(Paths.get("src/main/resources/certs/publicKey.pem"));
            String privateKeyContent = Files.readString(Paths.get("src/main/resources/certs/privateKey.pem"));
            
            System.out.println("Public key content length: " + publicKeyContent.length());
            System.out.println("Private key content length: " + privateKeyContent.length());
            
        } catch (NoSuchAlgorithmException | IOException e) {
            System.err.println("Error generating RSA keys: " + e.getMessage());
            e.printStackTrace();
        }
    }
}