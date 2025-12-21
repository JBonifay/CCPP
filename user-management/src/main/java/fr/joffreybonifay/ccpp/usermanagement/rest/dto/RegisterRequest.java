package fr.joffreybonifay.ccpp.usermanagement.rest.dto;

import jakarta.validation.constraints.Email;
import org.jspecify.annotations.NonNull;

public record RegisterRequest(
        @NonNull
        @Email
        String email,
        @NonNull
        String password,
        @NonNull
        String fullName
) {
}
