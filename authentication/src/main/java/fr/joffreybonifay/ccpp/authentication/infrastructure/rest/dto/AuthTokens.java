package fr.joffreybonifay.ccpp.authentication.infrastructure.rest.dto;

public record AuthTokens(
        String accessToken,
        String refreshToken
) {}
