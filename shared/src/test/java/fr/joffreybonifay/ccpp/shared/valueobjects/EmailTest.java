package fr.joffreybonifay.ccpp.shared.valueobjects;

import fr.joffreybonifay.ccpp.shared.exception.EmailFormatException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    void should_create_valid_email() {
        Email email = new Email("test@example.com");

        assertThat(email.value()).isEqualTo("test@example.com");
    }

    @Test
    void should_throw_exception_for_invalid_email() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(EmailFormatException.class)
                .hasMessageContaining("Invalid email");

        assertThatThrownBy(() -> new Email(""))
                .isInstanceOf(EmailFormatException.class)
                .hasMessageContaining("Invalid email");

        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(EmailFormatException.class)
                .hasMessageContaining("Invalid email");
    }

}
