package com.jimsa.garaappbackend.ws.services.impls;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.jimsa.garaappbackend.configs.auth.JwtUtil;
import com.jimsa.garaappbackend.configs.exception.AppServiceException;
import com.jimsa.garaappbackend.utils.Base64EncodedCredentialsUtils;
import com.jimsa.garaappbackend.ws.model.dtos.LoginDto;
import com.jimsa.garaappbackend.ws.model.dtos.TokenResponseDto;
import com.jimsa.garaappbackend.ws.model.entities.UserEntity;
import com.jimsa.garaappbackend.ws.repositories.UserRepository;
import com.jimsa.garaappbackend.ws.services.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.jimsa.garaappbackend.utils.constants.SecurityConstants.*;

@Slf4j
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final Base64EncodedCredentialsUtils base64EncodedCredentialsUtils;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;


    @Override
    public TokenResponseDto login(LoginDto loginDto) {

        log.info("Login attempt for user: {}", base64EncodedCredentialsUtils.getUsername(loginDto.getEncodedCredentials()));

        try {

            String username = base64EncodedCredentialsUtils.getUsername(loginDto.getEncodedCredentials());
            String password = base64EncodedCredentialsUtils.getPassword(loginDto.getEncodedCredentials());

            Optional<UserEntity> userOptional = userRepository.findByUsername(username);

            if (userOptional.isEmpty()) {
                log.warn("User not found: {}", username);
                throw new AppServiceException("Invalid credentials", HttpStatus.UNAUTHORIZED);
            }

            UserEntity user = userOptional.get();

            if (!passwordEncoder.matches(password, user.getEncryptedPassword())) {
                log.warn("Invalid password for user: {}", username);
                throw new AppServiceException("Invalid credentials", HttpStatus.UNAUTHORIZED);
            }

            String accessToken = jwtUtil.generateToken(
                    APP_SECURITY_JWT_TOKEN,
                    user.getUsername(),
                    "USER" // Default role
            );

            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

            log.info("Successful login for user: {}", user.getUsername());

            return TokenResponseDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(EXPIRATION_TIME / 1000) // Convert to seconds
                    .build();

        } catch (Exception exception) {
            log.error("Login error: {}", exception.getMessage(), exception);
            throw new AppServiceException("Login failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public boolean logout(String authHeader) {
        log.info("Logout attempt with header: {}", authHeader != null ? "***" : "null");

        try {
            if (authHeader == null || !authHeader.startsWith(APP_SECURITY_BEARER)) {
                log.warn("Invalid authorization header for logout");
                return false;
            }

            String token = authHeader.substring(APP_SECURITY_BEARER.length());

            // Verify and decode the JWT token
            DecodedJWT decodedJWT = jwtUtil.verifyToken(token);
            String username = decodedJWT.getClaim(APP_SECURITY_USERNAME).asString();

            // Revoke all refresh tokens for this user
            jwtUtil.revokeAllRefreshTokensForUser(username);

            log.info("Successful logout for user: {}", username);
            return true;

        } catch (Exception exception) {
            log.error("Logout error: {}", exception.getMessage(), exception);
            return false;
        }

    }

    @Override
    public TokenResponseDto refreshToken(String refreshToken) {
        log.info("Token refresh attempt");

        try {
            // Validate refresh token and get username
            String username = jwtUtil.validateRefreshToken(refreshToken);

            // Find user to get current details
            Optional<UserEntity> userOptional = userRepository.findByUsername(username);

            if (userOptional.isEmpty()) {
                log.warn("User not found during token refresh: {}", username);
                throw new AppServiceException("User not found", HttpStatus.UNAUTHORIZED);
            }

            UserEntity user = userOptional.get();

            // Generate new tokens
            String newAccessToken = jwtUtil.generateToken(
                    APP_SECURITY_JWT_TOKEN,
                    user.getUsername(),
                    "USER" // Default role, enhance with actual roles
            );

            String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());

            // Revoke the old refresh token
            jwtUtil.revokeRefreshToken(refreshToken);

            log.info("Successful token refresh for user: {}", user.getUsername());

            return TokenResponseDto.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .tokenType("Bearer")
                    .expiresIn(EXPIRATION_TIME / 1000)
                    .build();


        } catch (Exception exception) {
            log.error("Token refresh error: {}", exception.getMessage(), exception);
            throw new AppServiceException("Token refresh failed", HttpStatus.UNAUTHORIZED);
        }

    }

}
