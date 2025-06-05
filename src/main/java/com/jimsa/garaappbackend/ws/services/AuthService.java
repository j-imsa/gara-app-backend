package com.jimsa.garaappbackend.ws.services;

import com.jimsa.garaappbackend.ws.model.dtos.LoginDto;
import com.jimsa.garaappbackend.ws.model.dtos.TokenResponseDto;

public interface AuthService {
    TokenResponseDto login(LoginDto loginDto);

    boolean logout(String authHeader);

    TokenResponseDto refreshToken(String refreshToken);
}
