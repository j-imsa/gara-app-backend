package com.jimsa.garaappbackend.ws.services;

import com.jimsa.garaappbackend.ws.model.dtos.LoginDto;
import com.jimsa.garaappbackend.ws.model.dtos.TokenResponseDto;
import com.jimsa.garaappbackend.ws.model.dtos.UserDto;

public interface AuthService {
    TokenResponseDto login(LoginDto loginDto);

    boolean logout(String authHeader);

    TokenResponseDto refreshToken(String refreshToken);

    UserDto register(UserDto userDto);
}
