package com.jimsa.garaappbackend.ws.model.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ResponseDto {

    private boolean action;

    private Object result;

    @Override
    public String toString() {
        return "{"
               + "\"action\": " + action + ","
               + "\"result\": \"" + result + "\""
               + "}";
    }
}
