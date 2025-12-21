package fr.joffreybonifay.ccpp.usermanagement.application.command;

import fr.joffreybonifay.ccpp.shared.eventbus.EventBus;
import fr.joffreybonifay.ccpp.shared.eventbus.SimpleEventBus;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.shared.eventstore.InMemoryEventStore;
import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.valueobjects.Email;
import fr.joffreybonifay.ccpp.usermanagement.domain.event.UserCreated;
import fr.joffreybonifay.ccpp.usermanagement.domain.exception.UserCreationException;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.MockUserUniquenessChecker;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegisterNewUserCommandHandlerTest {

    MockUserUniquenessChecker uniquenessChecker = new MockUserUniquenessChecker();
    EventBus eventBus = new SimpleEventBus();
    EventStore eventStore = new InMemoryEventStore(eventBus);
    RegisterNewUserCommandHandler handler = new RegisterNewUserCommandHandler(eventStore, uniquenessChecker);

    UserId userId = new UserId(UUID.randomUUID());
    Email email = new Email("test@test.com");

    @Test
    void should_register_new_user() {
        handler.handle(new RegisterNewUserCommand(
                userId,
                email,
                "password",
                "fullname"
        ));

        assertThat(eventStore.loadEvents(userId.value()))
                .last()
                .isEqualTo(new UserCreated(userId, email, "password", "fullname"));
    }

    @Test
    void should_fail_if_email_taken() {
        uniquenessChecker.addEmail(email.value());

        assertThatThrownBy(() -> handler.handle(
                new RegisterNewUserCommand(
                        userId,
                        email,
                        "password",
                        "fullname"
                )))
                .isInstanceOf(UserCreationException.class)
                .hasMessage("Email already in use");
    }
}
