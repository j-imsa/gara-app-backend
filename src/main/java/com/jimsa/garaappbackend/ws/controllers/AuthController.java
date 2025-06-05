package com.jimsa.garaappbackend.ws.controllers;

import com.jimsa.garaappbackend.ws.model.dtos.LoginDto;
import com.jimsa.garaappbackend.ws.model.dtos.RefreshTokenRequestDto;
import com.jimsa.garaappbackend.ws.model.dtos.ResponseDto;
import com.jimsa.garaappbackend.ws.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import static com.jimsa.garaappbackend.utils.constants.SecurityConstants.APP_SECURITY_AUTHORIZATION;

@RestController
@RequestMapping(path = "/auth", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDto> login(@RequestBody LoginDto loginDto) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(ResponseDto.builder()
                        .action(true)
                        .result(authService.login(loginDto))
                        .build()
                );
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDto> logout(@RequestHeader(APP_SECURITY_AUTHORIZATION) String authHeader) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(ResponseDto.builder()
                        .action(true)
                        .result(authService.logout(authHeader))
                        .build()
                );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.builder()
                        .action(true)
                        .result(authService.refreshToken(refreshTokenRequest.getRefreshToken()))
                        .build()
                );
    }


}
