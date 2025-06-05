package com.jimsa.garaappbackend.utils.constants;

public final class SecurityConstants {
    private SecurityConstants() {
    }

    public static final String APP_SECURITY_AUTHORIZATION = "Authorization";
    public static final String APP_SECURITY_JWT_TOKEN = "JWT Token";
    public static final String APP_SECURITY_BEARER = "Bearer ";
    public static final String APP_SECURITY_ISSUER = "GaraAppBackend";
    public static final String APP_SECURITY_USERNAME = "username";
    public static final String APP_SECURITY_ROLE = "role";
    public static final String APP_SECURITY_ENVIRONMENT_PROPERTY = "app.security.key";
    public static final long EXPIRATION_TIME = 10000 * 60 * 1000; // 10 min (min * sec * millisecond)
}
