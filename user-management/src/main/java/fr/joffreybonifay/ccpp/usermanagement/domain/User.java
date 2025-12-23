package fr.joffreybonifay.ccpp.usermanagement.domain;

import fr.joffreybonifay.ccpp.shared.domain.AggregateRoot;
import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.Email;
import fr.joffreybonifay.ccpp.usermanagement.domain.event.UserAssignedToWorkspace;
import fr.joffreybonifay.ccpp.usermanagement.domain.event.UserCreated;
import fr.joffreybonifay.ccpp.usermanagement.domain.exception.UserCreationException;
import fr.joffreybonifay.ccpp.usermanagement.domain.exception.UserDoesNotExistException;
import fr.joffreybonifay.ccpp.usermanagement.domain.service.UserUniquenessChecker;

import java.util.List;

public class User extends AggregateRoot {

    public User() {
    }

    private User(List<DomainEvent> userEvents) {
        if (userEvents.isEmpty()) throw new UserDoesNotExistException("User does not exist");
        loadFromHistory(userEvents);
    }

    public static User create(UserId userId, Email email, String passwordHash, String fullname, UserUniquenessChecker uniquenessChecker) {
        if (uniquenessChecker.isEmailAlreadyInUse(email.value())) throw new UserCreationException("Email already in use");
        User user = new User();
        user.raiseEvent(new UserCreated(userId, email, passwordHash, fullname));
        return user;
    }

    public static User fromHistory(List<DomainEvent> userEvents) {
        return new User(userEvents);
    }

    public void assignToWorkspace(WorkspaceId workspaceId) {
        raiseEvent(new UserAssignedToWorkspace(new UserId(aggregateId), workspaceId));
    }

    @Override
    protected void apply(DomainEvent event) {
        switch (event) {
            case UserCreated userCreated -> apply(userCreated);
            case UserAssignedToWorkspace userAssignedToWorkspace -> apply(userAssignedToWorkspace);
            default -> throw new IllegalStateException("Unexpected value: " + event);
        }
    }

    private void apply(UserCreated userCreated) {
        this.aggregateId = userCreated.userId().value();
    }

    private void apply(UserAssignedToWorkspace userAssignedToWorkspace) {

    }

}
