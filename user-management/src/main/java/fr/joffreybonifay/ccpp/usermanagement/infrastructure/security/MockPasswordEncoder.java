package fr.joffreybonifay.ccpp.usermanagement.infrastructure.security;

import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MockPasswordEncoder implements PasswordEncoder {

    @Setter
    private boolean shouldMatch = true;

    @Override
    public @Nullable String encode(@Nullable CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(@Nullable CharSequence rawPassword, @Nullable String encodedPassword) {
        return shouldMatch;
    }

}
