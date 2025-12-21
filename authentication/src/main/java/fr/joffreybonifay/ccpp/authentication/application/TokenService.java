package fr.joffreybonifay.ccpp.authentication.application;

import fr.joffreybonifay.ccpp.authentication.infrastructure.rest.dto.AuthTokens;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class TokenService {

    private final Key accessKey;
    private final Key refreshKey;

    public TokenService() {
        this.accessKey = Jwts.SIG.HS256.key().build();
        this.refreshKey = Jwts.SIG.HS256.key().build();

    }

    public AuthTokens issue(UUID userId, String email, UUID workspaceId) {
        String accessToken = jwt(
                Map.of(
                        "sub", userId.toString(),
                        "email", email,
                        "workspaceId", workspaceId.toString()
                ),
                15, // minutes
                accessKey
        );

        String refreshToken = jwt(
                Map.of(
                        "sub", userId.toString(),
                        "workspaceId", workspaceId.toString()
                ),
                30 * 24 * 60, // minutes
                refreshKey
        );
        return new AuthTokens(accessToken, refreshToken);
    }

    private String jwt(Map<String, Object> claims, int expiresInMinutes, Key key) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expiresInMinutes * 60L)))
                .signWith(key)
                .compact();
    }
}
