package com.jimsa.garaappbackend.configs.exception.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimsa.garaappbackend.ws.model.dtos.ResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.jimsa.garaappbackend.utils.constants.ExceptionConstants.EXCEPTION_MESSAGE;

@Slf4j
@Component
@AllArgsConstructor
public class AppAuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        log.error("AppAuthenticationEntryPointHandler: {}", authException.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put(EXCEPTION_MESSAGE,authException.getMessage());

        ResponseDto responseDto = ResponseDto.builder()
                .action(false)
                .result(error)
                .build();

        String jsonResponse = objectMapper.writeValueAsString(responseDto);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(jsonResponse);
    }
}
