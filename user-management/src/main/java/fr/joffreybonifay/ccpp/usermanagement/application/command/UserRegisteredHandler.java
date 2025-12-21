package fr.joffreybonifay.ccpp.usermanagement.application.command;

import fr.joffreybonifay.ccpp.shared.event.UserRegisteredEvent;
import fr.joffreybonifay.ccpp.usermanagement.domain.User;
import fr.joffreybonifay.ccpp.usermanagement.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredHandler {

    private final UserRepository repository;

    public UserRegisteredHandler(UserRepository repository) {
        this.repository = repository;
    }

    public void handle(UserRegisteredEvent event) {

        if (repository.existsById(event.userId())) {
            // already processed → OK
            return;
        }

        User user = User.create(
                event.userId(),
                event.email(),
                "New user"
        );

        repository.save(user);
    }

}
