package fr.joffreybonifay.ccpp.usermanagement.infrastructure.rest.dto;

public record LoginRequest(
        String email,
        String password
) {}
