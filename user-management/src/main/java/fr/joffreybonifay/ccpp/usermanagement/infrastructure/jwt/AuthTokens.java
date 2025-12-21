package fr.joffreybonifay.ccpp.usermanagement.infrastructure.jwt;

public record AuthTokens(
        String accessToken,
        String refreshToken
) {}
