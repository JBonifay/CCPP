package fr.joffreybonifay.ccpp.usermanagement.infrastructure.projection;

import fr.joffreybonifay.ccpp.usermanagement.application.projection.UserProjectionHandler;
import fr.joffreybonifay.ccpp.usermanagement.application.query.model.UserDTO;
import fr.joffreybonifay.ccpp.usermanagement.application.query.repository.UserReadRepository;
import fr.joffreybonifay.ccpp.usermanagement.domain.event.UserAssignedToWorkspace;
import fr.joffreybonifay.ccpp.usermanagement.domain.event.UserCreated;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Component
public class UserProjectionUpdater implements UserProjectionHandler {

    private final UserReadRepository repository;

    public UserProjectionUpdater(UserReadRepository repository) {
        this.repository = repository;
    }

    @Override
    @EventListener
    @Transactional
    public void on(UserCreated event) {
        var dto = new UserDTO(
                event.userId(),
                event.email().value(),
                event.passwordHash(),
                event.fullname(),
                new HashSet<>()
        );
        repository.save(dto);
    }

    @Override
    @EventListener
    @Transactional
    public void on(UserAssignedToWorkspace event) {
        var current = repository.findById(event.userId())
                .orElseThrow(() -> new IllegalStateException(
                        "Missing projection for user " + event.userId()
                ));

        var updatedWorkspaces = new HashSet<>(current.workspaceIds());
        updatedWorkspaces.add(event.workspaceId().value());

        repository.update(new UserDTO(
                current.userId(),
                current.email(),
                current.passwordHash(),
                current.fullName(),
                updatedWorkspaces
        ));
    }
}
