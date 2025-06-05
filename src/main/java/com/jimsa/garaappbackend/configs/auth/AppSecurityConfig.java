package com.jimsa.garaappbackend.configs.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimsa.garaappbackend.configs.exception.handlers.AppAccessDeniedHandler;
import com.jimsa.garaappbackend.configs.exception.handlers.AppAuthenticationEntryPointHandler;
import com.jimsa.garaappbackend.configs.exception.handlers.AppAuthenticationErrorHandler;
import com.jimsa.garaappbackend.configs.front.FrontendProperties;
import com.jimsa.garaappbackend.ws.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static com.jimsa.garaappbackend.utils.constants.RouteConstants.*;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class AppSecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final FrontendProperties frontendProperties;
    private final ObjectMapper objectMapper;
    private final AppAuthenticationErrorHandler appAuthenticationErrorHandler;


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        sessionManagement ->
                                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .cors(
                        cors -> cors.configurationSource(request -> {
                            CorsConfiguration config = new CorsConfiguration();
                            config.setAllowCredentials(true);
                            config.setAllowedOrigins(frontendProperties.getOrigins());
                            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                            config.setAllowedHeaders(List.of(
                                    "Authorization", "Content-Type", "X-Requested-With",
                                    "Accept", "Origin", "Access-Control-Request-Method",
                                    "Access-Control-Request-Headers"
                            ));
                            config.setExposedHeaders(List.of("Authorization", "Authentication"));
                            config.setMaxAge(3600L);
                            return config;
                        })
                )
                .authorizeHttpRequests(authorizeRequests -> {
                    authorizeRequests.requestMatchers(HttpMethod.POST, ROUTE_LOGIN).permitAll();
                    authorizeRequests.requestMatchers(HttpMethod.POST, ROUTE_LOGOUT).permitAll();
                    authorizeRequests.requestMatchers(HttpMethod.POST, ROUTE_REGISTER).permitAll();
                    authorizeRequests.requestMatchers(HttpMethod.POST, ROUTE_REFRESH_TOKEN).permitAll();
                    authorizeRequests.requestMatchers("/public/**").permitAll();
                    authorizeRequests.requestMatchers("/actuator/**").permitAll();

                    authorizeRequests.requestMatchers("/**").authenticated();
                })
                .addFilterBefore(new AppAuthorizationFilter(jwtUtil, userRepository, appAuthenticationErrorHandler), BasicAuthenticationFilter.class);

        httpSecurity.httpBasic(hbc -> hbc.authenticationEntryPoint(new AppAuthenticationEntryPointHandler(objectMapper)));
        httpSecurity.exceptionHandling(ehc -> ehc.accessDeniedHandler(new AppAccessDeniedHandler()));


        return httpSecurity.build();
    }


    @Bean
    public PasswordEncoder passwordValidator() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("User details service is disabled");
        };
    }

}
