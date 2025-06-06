
package com.jimsa.garaappbackend.utils;

import com.jimsa.garaappbackend.ws.model.entities.UserEntity;
import com.jimsa.garaappbackend.ws.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class AuthenticationUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;

    private static final String USER_INFO_PREFIX = "user_info:";
    private static final long USER_INFO_TTL = 30; // 30 minutes

    /**
     * Get username directly from Authentication principal
     */
    public String getUsername(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        return authentication.getName(); // This gets the username from the principal
    }

    /**
     * Get role from Authentication authorities
     */
    public String getRole(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return null;
        }

        return authentication.getAuthorities().stream()
                .findFirst()
                .map(authority -> authority.getAuthority())
                .orElse(null);
    }

    /**
     * Get user public ID
     */
    public String getPublicId(Authentication authentication) {
        UserEntity user = getUserInfo(authentication);
        return user != null ? user.getPublicId() : null;
    }

    /**
     * Get full user information (with Redis caching)
     */
    public UserEntity getUserInfo(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        String username = getUsername(authentication);
        if (username == null) {
            return null;
        }

        // Try to get from Redis first
        String cacheKey = USER_INFO_PREFIX + username;
        UserEntity cachedUser = (UserEntity) redisTemplate.opsForValue().get(cacheKey);

        if (cachedUser != null) {
            return cachedUser;
        }

        // If not in cache, get from database
        UserEntity user = userRepository.findByUsername(username).orElse(null);

        if (user != null) {
            // Cache for future use
            redisTemplate.opsForValue().set(cacheKey, user, USER_INFO_TTL, TimeUnit.MINUTES);
        }

        return user;
    }

    /**
     * Get user email from cached user info
     */
    public String getEmail(Authentication authentication) {
        UserEntity user = getUserInfo(authentication);
        return user != null ? user.getEmail() : null;
    }

    /**
     * Get user ID from cached user info
     */
    public Long getUserId(Authentication authentication) {
        UserEntity user = getUserInfo(authentication);
        return user != null ? user.getId() : null;
    }

    /**
     * Check if user has specific role
     */
    public boolean hasRole(Authentication authentication, String role) {
        String userRole = getRole(authentication);
        return role != null && role.equals(userRole);
    }

    /**
     * Check if authentication is valid
     */
    public boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }

    /**
     * Invalidate user cache (call this when user info changes)
     */
    public void invalidateUserCache(String username) {
        String cacheKey = USER_INFO_PREFIX + username;
        redisTemplate.delete(cacheKey);
    }
}