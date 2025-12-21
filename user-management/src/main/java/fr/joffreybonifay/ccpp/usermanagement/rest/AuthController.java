package fr.joffreybonifay.ccpp.usermanagement.rest;

import fr.joffreybonifay.ccpp.usermanagement.jwt.AuthTokens;
import fr.joffreybonifay.ccpp.usermanagement.jwt.TokenService;
import fr.joffreybonifay.ccpp.usermanagement.repository.User;
import fr.joffreybonifay.ccpp.usermanagement.repository.UserRepository;
import fr.joffreybonifay.ccpp.usermanagement.rest.dto.LoginRequest;
import fr.joffreybonifay.ccpp.usermanagement.rest.dto.RegisterRequest;
import fr.joffreybonifay.ccpp.usermanagement.rest.dto.SelectWorkspaceRequest;
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
@RequestMapping("/auth")
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

    @PostMapping("/select-workspace")
    public ResponseEntity<AuthTokens> selectWorkspace(@RequestBody SelectWorkspaceRequest request) {
        // 1. Extract Identity from current Refresh Token
        Claims claims = tokenService.parseRefreshToken(request.refreshToken());
        UUID userId = UUID.fromString(claims.getSubject());
        String email = claims.get("email", String.class);

        // 2. IMPORTANT: Verify user actually belongs to this workspace
        // This prevents "token injection" where a user tries to access a workspace they don't own.
        // boolean hasAccess = workspaceService.checkAccess(userId, request.workspaceId());
        // if (!hasAccess) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        // 3. Issue the context-aware tokens
        AuthTokens tokens = tokenService.issue(userId, email, request.workspaceId());

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokens> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Conventional: Always start with a "Neutral" token on login
        // unless you want to implement "Remember Last Workspace" logic.
        AuthTokens tokens = tokenService.issue(user.getId(), user.getEmail());

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthTokens> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        Claims claims = tokenService.parseRefreshToken(refreshToken);
        String userId = claims.getSubject();
        String email = claims.get("email", String.class);

        // IMPORTANT: If the refresh token already had a workspaceId,
        // carry it over to the new access token so the user doesn't lose context.
        String workspaceIdStr = claims.get("workspaceId", String.class);
        UUID workspaceId = (workspaceIdStr != null) ? UUID.fromString(workspaceIdStr) : null;

        AuthTokens tokens = tokenService.issue(UUID.fromString(userId), email, workspaceId);

        return ResponseEntity.ok(tokens);
    }
}

