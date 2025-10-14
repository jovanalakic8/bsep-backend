package com.project.bsep.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.project.bsep.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.verificationExpiration}")
    private long jwtVerificationExpirationMs;

    @Value("${jwt.activationExpiration}")
    private long jwtActivationExpirationMs;

    public String generateAccessToken(Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        return generateAccessToken(user);
    }

    public String generateRefreshToken(Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        return generateRefreshToken(user);
    }

    public String generateAccessToken(User user) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim("username", user.getUsername())
                .withClaim("roles", user.getRoles().toString())
                .withClaim("id", user.getId())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date((new Date()).getTime() + jwtExpirationMs))
                .sign(Algorithm.HMAC256(jwtSecret));
    }

    public String generateRefreshToken(User user) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim("username", user.getUsername())
                .withClaim("roles", user.getRoles().toString())
                .withClaim("id", user.getId())
                .sign(Algorithm.HMAC256(jwtSecret));
    }

    public String generateVerificationToken(String username) {
        String token = JWT.create()
                .withSubject(username)
                .withClaim("username", username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date((new Date()).getTime() + jwtVerificationExpirationMs))
                .sign(Algorithm.HMAC256(jwtSecret));
        return token;

    }
    public String generateActivationToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withClaim("username", username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date((new Date()).getTime() + jwtActivationExpirationMs))
                .sign(Algorithm.HMAC256(jwtSecret));
    }
    public String getUserNameFromToken(String token) {
        return JWT.decode(token).getSubject();
    }

    public Date getExpirationDateFromToken(String token) {
        return JWT.decode(token).getExpiresAt();
    }

    public Boolean isTokenExpired(String token) {
        Date expirationTimestamp = getExpirationDateFromToken(token);
        Instant expirationTime = Instant.ofEpochMilli(expirationTimestamp.getTime());
        Instant currentTime = Instant.now();


        return expirationTime.isBefore(currentTime);
    }

    public boolean validateJwtToken(String authToken) {
        try {
            JWT.require(Algorithm.HMAC256(jwtSecret)).build().verify(authToken);
            return true;
        } catch(SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
