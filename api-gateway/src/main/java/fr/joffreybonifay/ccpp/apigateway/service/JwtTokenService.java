package fr.joffreybonifay.ccpp.apigateway.service;

import fr.joffreybonifay.ccpp.apigateway.AuthContext;
import fr.joffreybonifay.ccpp.apigateway.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenService {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMinutes;
    private final long refreshTokenExpirationDays;

    public JwtTokenService(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.access-token-expiration-minutes:15}") long accessTokenExpirationMinutes,
        @Value("${jwt.refresh-token-expiration-days:7}") long refreshTokenExpirationDays
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
        this.refreshTokenExpirationDays = refreshTokenExpirationDays;
    }

    public String generateAccessToken(UUID userId, UUID workspaceId) {
        Instant now = Instant.now();
        Instant expiration = now.plus(accessTokenExpirationMinutes, ChronoUnit.MINUTES);

        return Jwts.builder()
            .subject(userId.toString())
            .claim("workspaceId", workspaceId.toString())
            .claim("type", "access")
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact();
    }

    public String generateRefreshToken(UUID userId, UUID workspaceId) {
        Instant now = Instant.now();
        Instant expiration = now.plus(refreshTokenExpirationDays, ChronoUnit.DAYS);

        return Jwts.builder()
            .subject(userId.toString())
            .claim("workspaceId", workspaceId.toString())
            .claim("type", "refresh")
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact();
    }

    public Instant getRefreshTokenExpiration() {
        return Instant.now().plus(refreshTokenExpirationDays, ChronoUnit.DAYS);
    }

    public AuthContext validateAndExtract(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

            String tokenType = claims.get("type", String.class);
            if (!"access".equals(tokenType)) {
                throw new JwtException("Invalid token type");
            }

            UUID userId = UUID.fromString(claims.getSubject());
            UUID workspaceId = UUID.fromString(claims.get("workspaceId", String.class));

            return new AuthContext(userId, workspaceId);
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Invalid or expired token", e);
        }
    }

    public AuthContext validateRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

            String tokenType = claims.get("type", String.class);
            if (!"refresh".equals(tokenType)) {
                throw new JwtException("Invalid token type");
            }

            UUID userId = UUID.fromString(claims.getSubject());
            UUID workspaceId = UUID.fromString(claims.get("workspaceId", String.class));

            return new AuthContext(userId, workspaceId);
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Invalid or expired refresh token", e);
        }
    }
}
