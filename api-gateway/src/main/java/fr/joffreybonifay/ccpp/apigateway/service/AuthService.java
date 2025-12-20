package fr.joffreybonifay.ccpp.apigateway.service;

import fr.joffreybonifay.ccpp.apigateway.model.AuthContext;
import fr.joffreybonifay.ccpp.apigateway.exception.InvalidTokenException;
import fr.joffreybonifay.ccpp.apigateway.repository.RefreshToken;
import fr.joffreybonifay.ccpp.apigateway.repository.RefreshTokenRepository;
import fr.joffreybonifay.ccpp.apigateway.repository.User;
import fr.joffreybonifay.ccpp.apigateway.repository.UserRepository;
import fr.joffreybonifay.ccpp.apigateway.rest.dto.LoginRequest;
import fr.joffreybonifay.ccpp.apigateway.rest.dto.RegisterRequest;
import fr.joffreybonifay.ccpp.apigateway.rest.dto.TokenResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        String passwordHash = passwordEncoder.encode(request.password());
        User user = new User(request.email(), passwordHash, request.workspaceId());
        user = userRepository.save(user);

        return generateTokens(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        return generateTokens(user);
    }

    @Transactional
    public TokenResponse refreshToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token", null));

        if (refreshToken.isRevoked()) {
            throw new InvalidTokenException("Refresh token has been revoked", null);
        }

        AuthContext authContext = jwtTokenService.validateRefreshToken(refreshTokenValue);

        User user = userRepository.findById(authContext.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtTokenService.generateAccessToken(user.getId(), user.getWorkspaceId());

        return new TokenResponse(newAccessToken, refreshTokenValue, "Bearer", 15 * 60);
    }

    @Transactional
    public void logout(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    private TokenResponse generateTokens(User user) {
        String accessToken = jwtTokenService.generateAccessToken(user.getId(), user.getWorkspaceId());
        String refreshTokenValue = jwtTokenService.generateRefreshToken(user.getId(), user.getWorkspaceId());

        RefreshToken refreshToken = new RefreshToken(
                user.getId(),
                refreshTokenValue,
                jwtTokenService.getRefreshTokenExpiration()
        );
        refreshTokenRepository.save(refreshToken);

        return new TokenResponse(accessToken, refreshTokenValue, "Bearer", 15 * 60);
    }
}
