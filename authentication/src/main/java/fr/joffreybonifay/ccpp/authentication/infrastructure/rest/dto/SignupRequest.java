package fr.joffreybonifay.ccpp.authentication.infrastructure.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @Email
        @NotBlank
        String email,
        @NotBlank
        String workspaceId,
        @NotBlank
        String displayName,
        @NotBlank
        @Size(min = 8, max = 128)
        String password
) {
}
