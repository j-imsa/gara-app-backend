package com.jimsa.garaappbackend.ws.model.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    @JsonProperty("encoded_credentials")
    @NotBlank(message = "Encoded credentials cannot be empty")
    private String encodedCredentials;
}