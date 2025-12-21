package fr.joffreybonifay.ccpp.usermanagement.jwt;

public record AuthTokens(
        String accessToken,
        String refreshToken
) {}
