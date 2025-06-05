package com.jimsa.garaappbackend.configs.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jimsa.garaappbackend.configs.exception.AppServiceException;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.jimsa.garaappbackend.utils.constants.SecurityConstants.*;

@Component
@AllArgsConstructor
public class JwtUtil {

    private final Environment environment;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLISTED_TOKEN_PREFIX = "blacklisted_token:";
    private static final String USER_ACTIVE_TOKENS_PREFIX = "user_tokens:";

    public String generateToken(String subject, String username, String role) {

        String secKey = environment.getProperty(APP_SECURITY_ENVIRONMENT_PROPERTY);

        if (secKey == null || secKey.isEmpty()) {
            throw new AppServiceException("Jwt security token is empty or null", HttpStatus.UNAUTHORIZED);
        } else {
            String token = JWT.create()
                    .withIssuer(APP_SECURITY_ISSUER)
                    .withSubject(subject)
                    .withClaim(APP_SECURITY_USERNAME, username)
                    .withClaim(APP_SECURITY_ROLE, role)
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .sign(Algorithm.HMAC256(secKey));

            // Track this token for the user
            trackUserToken(username, token);

            return token;
        }
    }

    public DecodedJWT verifyToken(String token) throws JWTVerificationException {
        String secKey = environment.getProperty(APP_SECURITY_ENVIRONMENT_PROPERTY);
        if (secKey == null || secKey.isEmpty()) {
            throw new AppServiceException("Jwt security token is empty or null", HttpStatus.UNAUTHORIZED);
        }

        // First check if token is blacklisted
        if (isTokenBlacklisted(token)) {
            throw new JWTVerificationException("Token has been revoked");
        }

        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secKey))
                .withIssuer(APP_SECURITY_ISSUER)
                .build();
        return verifier.verify(token);
    }

    private boolean isTokenBlacklisted(String token) {
        String key = BLACKLISTED_TOKEN_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void blacklistToken(String token, long expirationTime) {
        String key = BLACKLISTED_TOKEN_PREFIX + token;
        // Store until the token's natural expiration
        long ttl = Math.max(0, expirationTime - System.currentTimeMillis());
        if (ttl > 0) {
            redisTemplate.opsForValue().set(key, "blacklisted", ttl, TimeUnit.MILLISECONDS);
        }
    }

    private void trackUserToken(String username, String token) {
        String key = USER_ACTIVE_TOKENS_PREFIX + username;
        redisTemplate.opsForSet().add(key, token);
        // Set expiration for the user's token set
        redisTemplate.expire(key, EXPIRATION_TIME, TimeUnit.MILLISECONDS);
    }

    public void blacklistAllAccessTokensForUser(String username) {
        String key = USER_ACTIVE_TOKENS_PREFIX + username;
        Set<String> activeTokens = redisTemplate.opsForSet().members(key);

        if (activeTokens != null) {
            for (String token : activeTokens) {
                try {
                    // Decode to get expiration time
                    DecodedJWT decodedJWT = JWT.decode(token);
                    Date expirationDate = decodedJWT.getExpiresAt();
                    blacklistToken(token, expirationDate.getTime());
                } catch (Exception e) {
                    // If token can't be decoded, blacklist it with default TTL
                    blacklistToken(token, System.currentTimeMillis() + EXPIRATION_TIME);
                }
            }
            // Clear the user's active tokens set
            redisTemplate.delete(key);
        }
    }

    public String generateRefreshToken(String username) {
        String refreshToken = UUID.randomUUID().toString();
        String key = REFRESH_TOKEN_PREFIX + refreshToken;

        redisTemplate.opsForValue().set(key, username, REFRESH_EXPIRATION_TIME, TimeUnit.MILLISECONDS);

        return refreshToken;
    }

    public String validateRefreshToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        String username = redisTemplate.opsForValue().get(key);

        if (username == null) {
            throw new AppServiceException("Invalid or expired refresh token", HttpStatus.UNAUTHORIZED);
        }

        return username;
    }

    public void revokeRefreshToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.delete(key);
    }

    public void revokeAllRefreshTokensForUser(String username) {
        String pattern = REFRESH_TOKEN_PREFIX + "*";
        redisTemplate.keys(pattern).forEach(key -> {
            String storedUsername = redisTemplate.opsForValue().get(key);
            if (username.equals(storedUsername)) {
                redisTemplate.delete(key);
            }
        });
    }
}