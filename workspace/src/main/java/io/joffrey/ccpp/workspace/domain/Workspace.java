package io.joffrey.ccpp.workspace.domain;

import com.ccpp.shared.domain.AggregateRoot;
import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.WorkspaceId;
import io.joffrey.ccpp.workspace.domain.event.WorkspaceCreated;
import io.joffrey.ccpp.workspace.domain.event.WorkspaceSubscriptionUpgraded;
import io.joffrey.ccpp.workspace.domain.exception.InvalidWorkspaceDataException;
import io.joffrey.ccpp.workspace.domain.exception.MembershipException;
import io.joffrey.ccpp.workspace.domain.model.Membership;

import java.util.List;

public class Workspace extends AggregateRoot {

    private Membership currentMembership;

    public Workspace(List<DomainEvent> workspaceDomainEvents) {
        loadFromHistory(workspaceDomainEvents);
    }

    private Workspace(WorkspaceId workspaceId, String workspaceName) {
        validateWorkspaceId(workspaceId);
        validateWorkspaceName(workspaceName);

        raiseEvent(new WorkspaceCreated(workspaceId, workspaceName, Membership.FREEMIUM));
    }

    public static Workspace create(WorkspaceId workspaceId, String workspaceName) {
        return new Workspace(workspaceId, workspaceName);
    }

    public static Workspace fromHistory(List<DomainEvent> workspaceEvents) {
        return new Workspace(workspaceEvents);
    }

    private void validateWorkspaceId(WorkspaceId workspaceId) {
        if (workspaceId == null || workspaceId.value() == null)
            throw new InvalidWorkspaceDataException("Workspace id cannot be empty");
    }

    private void validateWorkspaceName(String workspaceName) {
        if (workspaceName == null || workspaceName.isBlank())
            throw new InvalidWorkspaceDataException("Workspace name cannot be empty");
    }

    public void upgradeMembership() {
        if (currentMembership == Membership.PREMIUM) throw new MembershipException("Already premium member");
        raiseEvent(new WorkspaceSubscriptionUpgraded(new WorkspaceId(aggregateId), Membership.PREMIUM));
    }

    @Override
    protected void apply(DomainEvent event) {
        switch (event) {
            case WorkspaceCreated workspaceCreated -> apply(workspaceCreated);
            case WorkspaceSubscriptionUpgraded workspaceSubscriptionUpgraded -> apply(workspaceSubscriptionUpgraded);
            default -> throw new IllegalStateException("Unexpected value: " + event);
        }
    }

    private void apply(WorkspaceCreated workspaceCreated) {
        aggregateId = workspaceCreated.workspaceId().value();
    }

    private void apply(WorkspaceSubscriptionUpgraded workspaceSubscriptionUpgraded) {
        this.currentMembership = workspaceSubscriptionUpgraded.newMembership();
    }

}
