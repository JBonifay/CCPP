package fr.joffreybonifay.ccpp.authentication.application;

import fr.joffreybonifay.ccpp.authentication.domain.Account;
import fr.joffreybonifay.ccpp.authentication.domain.repository.AccountRepository;
import fr.joffreybonifay.ccpp.authentication.infrastructure.messaging.UserEventPublisher;
import fr.joffreybonifay.ccpp.authentication.infrastructure.rest.dto.SignupRequest;
import fr.joffreybonifay.ccpp.shared.event.UserRegisteredEvent;
import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.valueobjects.Email;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SignupHandler {

    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserEventPublisher publisher;

    public SignupHandler(
            AccountRepository repository,
            PasswordEncoder passwordEncoder,
            UserEventPublisher publisher
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.publisher = publisher;
    }

    public void handle(SignupRequest request) {
        repository.findByEmail(request.email())
                .ifPresent(a -> {
                    throw new IllegalStateException("Email already exists");
                });

        var account = Account.create(
                new Email(request.email()),
                new WorkspaceId(UUID.fromString(request.workspaceId())),
                request.displayName(),
                passwordEncoder.encode(request.password())
        );
        repository.save(account);

        publisher.publish(new UserRegisteredEvent(new UserId(account.userId()), account.email(), account.displayName()));
    }
}
