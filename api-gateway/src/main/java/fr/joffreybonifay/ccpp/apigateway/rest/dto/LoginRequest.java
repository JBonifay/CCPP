package fr.joffreybonifay.ccpp.apigateway.rest.dto;

public record LoginRequest(
        String email,
        String password
) {
}
