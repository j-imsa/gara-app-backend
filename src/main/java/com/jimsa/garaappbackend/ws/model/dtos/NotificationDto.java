package com.jimsa.garaappbackend.ws.model.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDto {

    @JsonProperty("target_user_public_id")
    private String targetUserPublicId;

    private String message;
}
