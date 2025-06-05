package com.jimsa.garaappbackend.configs.exception.handlers;

import com.jimsa.garaappbackend.configs.exception.AppServiceException;
import com.jimsa.garaappbackend.ws.model.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static com.jimsa.garaappbackend.utils.constants.ExceptionConstants.EXCEPTION_MESSAGE;

@Slf4j
@RestControllerAdvice
public class AppServiceExceptionHandler {

    @ExceptionHandler(value = AppServiceException.class)
    public ResponseEntity<ResponseDto> handleAppServiceException(AppServiceException ex) {

        log.error("AppServiceException: {}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put(EXCEPTION_MESSAGE, ex.getMessage());


        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(ResponseDto.builder()
                        .action(false)
                        .result(error)
                        .build());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ResponseDto> handleExceptions(Exception ex) {

        log.error("Exception: {}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put(EXCEPTION_MESSAGE, ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.builder()
                        .action(false)
                        .result(error)
                        .build());
    }
}
