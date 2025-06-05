package com.jimsa.garaappbackend.utils;

import com.jimsa.garaappbackend.configs.exception.AppServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class Base64EncodedCredentialsUtils {

    public String getUsername(String encodedCredentials) {
        String[] credentials = decodeCredentials(encodedCredentials);
        return credentials.length > 0 ? credentials[0] : null;
    }

    public String getPassword(String encodedCredentials) {
        String[] credentials = decodeCredentials(encodedCredentials);
        return credentials.length > 1 ? credentials[1] : null;
    }

    private String[] decodeCredentials(String encodedCredentials) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedCredentials);
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
            return decodedString.split(":", 2);
        } catch (Exception e) {
            log.error("Invalid encoded credentials format: {}", e.getMessage());
            throw new AppServiceException("Invalid encoded credentials format", HttpStatus.BAD_REQUEST);
        }
    }
}
