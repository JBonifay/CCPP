package fr.joffreybonifay.ccpp.authentication.application;

import fr.joffreybonifay.ccpp.authentication.domain.repository.AccountRepository;
import fr.joffreybonifay.ccpp.authentication.infrastructure.rest.dto.AuthTokens;
import fr.joffreybonifay.ccpp.authentication.infrastructure.rest.dto.LoginRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class LoginHandler {

    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public LoginHandler(
            AccountRepository repository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public AuthTokens handle(LoginRequest request) {
        var account = repository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), account.passwordHash())) {
            throw new IllegalStateException("Invalid credentials");
        }

        return tokenService.issue(account.userId(), account.email().value(), account.workspaceId().value());
    }
}
