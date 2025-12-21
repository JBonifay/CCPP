package fr.joffreybonifay.ccpp.usermanagement.rest;

import fr.joffreybonifay.ccpp.usermanagement.jwt.AuthTokens;
import fr.joffreybonifay.ccpp.usermanagement.jwt.TokenService;
import fr.joffreybonifay.ccpp.usermanagement.repository.User;
import fr.joffreybonifay.ccpp.usermanagement.repository.UserRepository;
import fr.joffreybonifay.ccpp.usermanagement.rest.dto.LoginRequest;
import fr.joffreybonifay.ccpp.usermanagement.rest.dto.RegisterRequest;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthTokens> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        User user = new User(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.fullName()
        );
        userRepository.save(user);

        AuthTokens tokens = tokenService.issue(user.getId(), user.getEmail());
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokens> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        AuthTokens tokens = tokenService.issue(user.getId(), user.getEmail());

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthTokens> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        Claims claims = tokenService.parseRefreshToken(refreshToken);
        String userId = claims.getSubject();
        String email = claims.get("email", String.class);

        AuthTokens tokens = tokenService.issue(UUID.fromString(userId), email);

        return ResponseEntity.ok(tokens);
    }
}

