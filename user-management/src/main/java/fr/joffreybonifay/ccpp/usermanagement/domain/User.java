package fr.joffreybonifay.ccpp.usermanagement.domain;

import fr.joffreybonifay.ccpp.shared.aggregate.AggregateRoot;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.usermanagement.domain.event.UserAssignedToWorkspace;
import fr.joffreybonifay.ccpp.usermanagement.domain.event.UserCreated;

import java.util.List;

public class User extends AggregateRoot {

    private User(List<DomainEvent> userEvents) {
        loadFromHistory(userEvents);
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
