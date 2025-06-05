package com.jimsa.garaappbackend.configs.exception.handlers;

import com.jimsa.garaappbackend.ws.model.dtos.ResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.jimsa.garaappbackend.utils.constants.ExceptionConstants.EXCEPTION_MESSAGE;

@Slf4j
@Component
public class AppAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.error("AppAccessDeniedHandler: {}", accessDeniedException.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put(EXCEPTION_MESSAGE,accessDeniedException.getLocalizedMessage());

        ResponseDto responseDto = ResponseDto.builder()
                .action(false)
                .result(error)
                .build();

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(responseDto.toString());
    }
}