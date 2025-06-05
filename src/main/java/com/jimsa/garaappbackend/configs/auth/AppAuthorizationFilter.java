package com.jimsa.garaappbackend.configs.auth;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jimsa.garaappbackend.configs.exception.handlers.AppAuthenticationErrorHandler;
import com.jimsa.garaappbackend.ws.model.entities.UserEntity;
import com.jimsa.garaappbackend.ws.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.jimsa.garaappbackend.utils.constants.RouteConstants.*;
import static com.jimsa.garaappbackend.utils.constants.SecurityConstants.*;


@Slf4j
@AllArgsConstructor
public class AppAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AppAuthenticationErrorHandler appAuthenticationErrorHandler;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.debug("JWTTokenValidatorFilter - Processing request: {} {}", request.getMethod(), request.getServletPath());

        // For public endpoints, skip JWT validation entirely
        if (!isProtectedEndpoint(request)) {
            log.debug("Skipping JWT validation for public endpoint: {}", request.getServletPath());
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader(APP_SECURITY_AUTHORIZATION);

        if (token != null && token.startsWith(APP_SECURITY_BEARER)) {

            try {
                // Extract token without "Bearer " prefix
                token = token.substring(APP_SECURITY_BEARER.length()).trim();

                // Verify and decode JWT token
                DecodedJWT jwt = jwtUtil.verifyToken(token);
                String username = jwt.getClaim(APP_SECURITY_USERNAME).asString();
                String role = jwt.getClaim(APP_SECURITY_ROLE).asString();

                // Validate that user still exists in database
                Optional<UserEntity> userOptional = userRepository.findByUsername(username);
                if (userOptional.isEmpty()) {
                    log.warn("Token valid but user not found: {}", username);
                    appAuthenticationErrorHandler.handleAuthenticationError(response, "User not found", HttpStatus.UNAUTHORIZED);
                    return;
                }

                UserEntity user = userOptional.get();

                // Create authentication object and set in security context
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of(new SimpleGrantedAuthority(role))
                        );

                // Add user details to authentication for later use
                authentication.setDetails(user);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Successfully authenticated user: {} with role: {}", username, role);

            } catch (JWTVerificationException e) {
                log.warn("JWT verification failed: {}", e.getMessage());
                appAuthenticationErrorHandler.handleAuthenticationError(response, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
                return;

            } catch (UsernameNotFoundException e) {
                log.warn("User not found: {}", e.getMessage());
                appAuthenticationErrorHandler.handleAuthenticationError(response, "User not found", HttpStatus.UNAUTHORIZED);
                return;

            } catch (Exception e) {
                log.error("Unexpected error during JWT validation: {}", e.getMessage(), e);
                appAuthenticationErrorHandler.handleAuthenticationError(response, "Authentication failed", HttpStatus.UNAUTHORIZED);
                return;
            }

        } else {
            // No token provided for protected endpoints
            log.warn("No JWT token found for protected endpoint: {}", request.getServletPath());
            appAuthenticationErrorHandler.handleAuthenticationError(response, "Authentication required", HttpStatus.UNAUTHORIZED);
            return;
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }


    private boolean isProtectedEndpoint(HttpServletRequest request) {
        String path = request.getServletPath();

        // List of public endpoints that don't require authentication
        return !path.equals(ROUTE_LOGIN) &&
               !path.equals(ROUTE_REGISTER) &&
               !path.equals(ROUTE_REFRESH_TOKEN) &&
               !path.startsWith("/public") &&
               !path.startsWith("/actuator");
    }

}
