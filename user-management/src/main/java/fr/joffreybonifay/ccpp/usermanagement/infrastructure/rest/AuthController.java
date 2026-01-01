package fr.joffreybonifay.ccpp.usermanagement.infrastructure.rest;

import fr.joffreybonifay.ccpp.shared.command.CommandBus;
import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.Email;
import fr.joffreybonifay.ccpp.shared.rest.RequestContext;
import fr.joffreybonifay.ccpp.usermanagement.application.command.RegisterNewUserCommand;
import fr.joffreybonifay.ccpp.usermanagement.application.query.model.UserDTO;
import fr.joffreybonifay.ccpp.usermanagement.application.query.repository.UserReadRepository;
import fr.joffreybonifay.ccpp.usermanagement.domain.exception.UserDoesNotExistException;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.jwt.AuthTokens;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.jwt.TokenService;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.rest.dto.LoginRequest;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.rest.dto.RegisterRequest;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.rest.dto.SelectWorkspaceRequest;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.rest.dto.UserDetailsResponse;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final CommandBus commandBus;
    private final UserReadRepository userReadRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthController(
            CommandBus commandBus,
            UserReadRepository userReadRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService
    ) {
        this.commandBus = commandBus;
        this.userReadRepository = userReadRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthTokens> register(@RequestBody RegisterRequest request) {
        UUID userId = UUID.randomUUID();
        commandBus.execute(new RegisterNewUserCommand(
                UUID.randomUUID(),
                new UserId(userId),
                new Email(request.email()),
                request.password(),
                request.fullName(),
                UUID.randomUUID(),
                null
        ));
        AuthTokens tokens = tokenService.issue(userId);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/select-workspace")
    public ResponseEntity<AuthTokens> selectWorkspace(@RequestBody SelectWorkspaceRequest request) {
        // 1. Extract Identity from current Refresh Token
        Claims claims = tokenService.parseRefreshToken(request.refreshToken());
        UUID userId = UUID.fromString(claims.getSubject());

        // 2. IMPORTANT: Verify user actually belongs to this workspace
        // This prevents "token injection" where a user tries to access a workspace they don't own.
        // boolean hasAccess = workspaceService.checkAccess(userId, request.workspaceId());
        // if (!hasAccess) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        // 3. Issue the context-aware tokens
        AuthTokens tokens = tokenService.issue(userId, request.workspaceId());

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokens> login(@RequestBody LoginRequest request) {
        UserDTO user = userReadRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Conventional: Always start with a "Neutral" token on login
        // unless you want to implement "Remember Last Workspace" logic.
        AuthTokens tokens = tokenService.issue(user.userId().value());

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthTokens> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        Claims claims = tokenService.parseRefreshToken(refreshToken);
        String userId = claims.getSubject();

        // IMPORTANT: If the refresh token already had a workspaceId,
        // carry it over to the new access token so the user doesn't lose context.
        String workspaceIdStr = claims.get("workspaceId", String.class);
        UUID workspaceId = (workspaceIdStr != null) ? UUID.fromString(workspaceIdStr) : null;

        AuthTokens tokens = tokenService.issue(UUID.fromString(userId), workspaceId);

        return ResponseEntity.ok(tokens);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDetailsResponse> me() {
        UserDTO userDTO = userReadRepository.findById(RequestContext.getUserId()).orElseThrow(() -> new UserDoesNotExistException("User not found"));
        return ResponseEntity.ok(new UserDetailsResponse(
                userDTO.userId(),
                userDTO.email(),
                userDTO.fullName(),
                "user",
                userDTO.workspaces()
        ));
    }
}
