package fr.joffreybonifay.ccpp.apigateway.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JwtWorkspaceUserFilterFactoryTest {

    private WebTestClient webTestClient;

    @org.springframework.boot.test.web.server.LocalServerPort
    private int port;

    @jakarta.annotation.PostConstruct
    public void init() {
        this.webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Test
    void publicEndpointsShouldBeAccessible() {
        webTestClient.post().uri("/auth/login")
                .exchange()
                .expectStatus().isBadRequest();
        
        // We check that it's NOT 401, because 404/500 might happen due to missing actuators in test
        webTestClient.get().uri("/actuator/health")
                .exchange()
                .expectStatus().value(status -> org.junit.jupiter.api.Assertions.assertNotEquals(401, status));
    }

    @Test
    void protectedEndpointsShouldRequireJwt() {
        webTestClient.get().uri("/workspaces/1")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void protectedEndpointsShouldBeAccessibleWithValidJwt() {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("user-123")
                .claim("workspaceId", "ws-456")
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60))
                .signWith(key, Jwts.SIG.HS256)
                .compact();

        webTestClient.get().uri("/workspaces/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().is5xxServerError(); // 5xx because the backend service is not running
    }
}
