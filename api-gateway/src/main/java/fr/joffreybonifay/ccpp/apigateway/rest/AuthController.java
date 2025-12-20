package fr.joffreybonifay.ccpp.apigateway.rest;

import fr.joffreybonifay.ccpp.apigateway.rest.dto.LoginRequest;
import fr.joffreybonifay.ccpp.apigateway.rest.dto.RefreshTokenRequest;
import fr.joffreybonifay.ccpp.apigateway.rest.dto.RegisterRequest;
import fr.joffreybonifay.ccpp.apigateway.rest.dto.TokenResponse;
import fr.joffreybonifay.ccpp.apigateway.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody RegisterRequest request) {
        TokenResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        TokenResponse response = authService.refreshToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("X-User-Id") UUID userId) {
        authService.logout(userId);
        return ResponseEntity.noContent().build();
    }
}
