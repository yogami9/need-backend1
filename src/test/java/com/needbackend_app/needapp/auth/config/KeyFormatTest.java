package com.needbackend_app.needapp.auth.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class KeyFormatTest {
    
    public static void main(String[] args) {
        try {
            // Path to your private key file
            String privateKeyPath = "src/main/resources/certs/privateKey.pem";
            
            // Read the file and print its content for inspection
            byte[] fileBytes = Files.readAllBytes(Paths.get(privateKeyPath));
            String fileContent = new String(fileBytes, StandardCharsets.UTF_8);
            
            System.out.println("Original file content:");
            System.out.println(fileContent);
            System.out.println("File length: " + fileContent.length());
            
            // Print hex representation to check for hidden characters
            System.out.println("\nHex representation of the file:");
            for (byte b : fileBytes) {
                System.out.printf("%02X ", b);
            }
            System.out.println();
            
            // Create a clean version of the key
            String cleanedKey = fileContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
                
            System.out.println("\nCleaned key (Base64 only):");
            System.out.println(cleanedKey);
            
            // Fix the key by creating a new file with proper format
            String fixedKey = "-----BEGIN PRIVATE KEY-----\n" +
                              formatBase64(cleanedKey) +
                              "\n-----END PRIVATE KEY-----";
            
            System.out.println("\nFixed key:");
            System.out.println(fixedKey);
            
            // Write the fixed key to a new file
            String fixedKeyPath = "src/main/resources/certs/privateKey_fixed.pem";
            Files.write(Paths.get(fixedKeyPath), fixedKey.getBytes(StandardCharsets.UTF_8));
            System.out.println("\nFixed key written to: " + fixedKeyPath);
            
            // Try to load the fixed key
            System.out.println("\nTesting fixed key loading...");
            String fixedKeyContent = new String(Files.readAllBytes(Paths.get(fixedKeyPath)));
            String base64Key = fixedKeyContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
            
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
            
            System.out.println("Fixed key loaded successfully!");
            System.out.println("Key format: " + privateKey.getFormat());
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Helper method to format Base64 string with line breaks every 64 characters
    private static String formatBase64(String base64) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < base64.length()) {
            int end = Math.min(i + 64, base64.length());
            result.append(base64.substring(i, end)).append("\n");
            i = end;
        }
        return result.toString();
    }
}