package com.jimsa.garaappbackend.configs.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jimsa.garaappbackend.configs.exception.AppServiceException;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.jimsa.garaappbackend.utils.constants.ExceptionConstants.EXCEPTION_REPORT_MESSAGE;
import static com.jimsa.garaappbackend.utils.constants.SecurityConstants.*;

@Component
@AllArgsConstructor
public class JwtUtil {

    private final Environment environment;

    public String generateToken(String subject, String username, String role) {
        String secKey = environment.getProperty(APP_SECURITY_ENVIRONMENT_PROPERTY);
        if (secKey == null || secKey.isEmpty()) {
            throw new AppServiceException(String.format(EXCEPTION_REPORT_MESSAGE, "A0001"), HttpStatus.UNAUTHORIZED);
        } else {
            return JWT.create()
                    .withIssuer(APP_SECURITY_ISSUER)
                    .withSubject(subject)
                    .withClaim(APP_SECURITY_USERNAME, username)
                    .withClaim(APP_SECURITY_ROLE, role)
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .sign(Algorithm.HMAC256(secKey));
        }
    }

    public DecodedJWT verifyToken(String token) throws JWTVerificationException {
        String secKey = environment.getProperty(APP_SECURITY_ENVIRONMENT_PROPERTY);
        if (secKey == null || secKey.isEmpty()) {
            throw new AppServiceException(String.format(EXCEPTION_REPORT_MESSAGE, "A0002"), HttpStatus.UNAUTHORIZED);
        } else {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secKey))
                    .withIssuer(APP_SECURITY_ISSUER)
                    .build();
            return verifier.verify(token);
        }
    }
}
