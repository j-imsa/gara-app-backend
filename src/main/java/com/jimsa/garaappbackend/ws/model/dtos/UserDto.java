package com.jimsa.garaappbackend.ws.model.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;


@Builder
@AllArgsConstructor
public class UserDto {
    @JsonProperty("public_id")
    private String publicId;
    private String username;
    private String email;
    private String name;
    private String family;
    @JsonProperty("phone_number")
    private String phoneNumber;
}


