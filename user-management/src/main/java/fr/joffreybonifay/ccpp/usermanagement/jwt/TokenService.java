package fr.joffreybonifay.ccpp.usermanagement.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class TokenService {

    private static final int ACCESS_TOKEN_EXPIRATION_MINUTES = 15;
    private static final int REFRESH_TOKEN_EXPIRATION_MINUTES = 30 * 24 * 60; // 30 days

    private final SecretKey key;

    public TokenService(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Issue both access and refresh tokens for a user
     */
    public AuthTokens issue(UUID userId, String email, UUID workspaceId) {
        String workspaceIdStr = workspaceId != null ? workspaceId.toString() : null;

        String accessToken = jwt(Map.of(
                "sub", userId.toString(),
                "email", email,
                "workspaceId", workspaceIdStr,
                "type", "access"
        ), ACCESS_TOKEN_EXPIRATION_MINUTES);

        String refreshToken = jwt(Map.of(
                "sub", userId.toString(),
                "workspaceId", workspaceIdStr,
                "type", "refresh"
        ), REFRESH_TOKEN_EXPIRATION_MINUTES);

        return new AuthTokens(accessToken, refreshToken);
    }

    public AuthTokens issue(UUID userId, String email) {
        String accessToken = jwt(Map.of(
                "sub", userId.toString(),
                "email", email,
                "type", "access"
        ), ACCESS_TOKEN_EXPIRATION_MINUTES);

        String refreshToken = jwt(Map.of(
                "sub", userId.toString(),
                "type", "refresh"
        ), REFRESH_TOKEN_EXPIRATION_MINUTES);

        return new AuthTokens(accessToken, refreshToken);
    }

    /**
     * Helper to build JWT
     */
    private String jwt(Map<String, Object> claims, int expiresInMinutes) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expiresInMinutes * 60L)))
                .signWith(key) // HS256 by default
                .compact();
    }

    /**
     * Parse a refresh token and return its claims
     */
    public Claims parseRefreshToken(String refreshToken) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        if (!"refresh".equals(claims.get("type", String.class))) {
            throw new RuntimeException("Not a refresh token");
        }

        return claims;
    }

    /**
     * Optional: parse access token
     */
    public Claims parseAccessToken(String accessToken) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

        if (!"access".equals(claims.get("type", String.class))) {
            throw new RuntimeException("Not an access token");
        }

        return claims;
    }
}
