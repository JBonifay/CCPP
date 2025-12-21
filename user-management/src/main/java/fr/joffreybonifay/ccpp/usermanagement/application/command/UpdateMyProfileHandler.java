package fr.joffreybonifay.ccpp.usermanagement.application.command;

import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.usermanagement.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UpdateMyProfileHandler {

    private final UserRepository repository;

    public UpdateMyProfileHandler(UserRepository repository) {
        this.repository = repository;
    }

    public void handle(UpdateMyProfileCommand command) {
        var user = repository.findById(new UserId(UUID.fromString(command.userId())))
                .orElseThrow(() -> new IllegalStateException("User not found"));

        user.updateProfile(command.displayName());
        repository.save(user);
    }
}
