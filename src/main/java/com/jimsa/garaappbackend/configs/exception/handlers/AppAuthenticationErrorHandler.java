package com.jimsa.garaappbackend.configs.exception.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimsa.garaappbackend.ws.model.dtos.ResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.jimsa.garaappbackend.utils.constants.ExceptionConstants.EXCEPTION_MESSAGE;

@Slf4j
@Component
@AllArgsConstructor
public class AppAuthenticationErrorHandler {

    private final ObjectMapper objectMapper;

    public void handleAuthenticationError(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, String> error = new HashMap<>();
        error.put(EXCEPTION_MESSAGE, message);

        ResponseDto errorResponse = ResponseDto.builder()
                .action(false)
                .result(error)
                .build();

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();

        // Clear security context to ensure no partial authentication
        SecurityContextHolder.clearContext();
    }
}
