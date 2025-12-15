package io.joffrey.ccpp.workspace.domain;

import com.ccpp.shared.domain.AggregateRoot;
import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.WorkspaceId;
import io.joffrey.ccpp.workspace.domain.event.WorkspaceCreated;
import io.joffrey.ccpp.workspace.domain.event.WorkspaceProjectCreationApproved;
import io.joffrey.ccpp.workspace.domain.event.WorkspaceSubscriptionUpgraded;
import io.joffrey.ccpp.workspace.domain.exception.InvalidWorkspaceDataException;
import io.joffrey.ccpp.workspace.domain.exception.SubscriptionTierException;
import io.joffrey.ccpp.workspace.domain.exception.ProjectLimitReachedException;
import io.joffrey.ccpp.workspace.domain.model.SubscriptionTier;

import java.util.List;

public class Workspace extends AggregateRoot {

    private SubscriptionTier currentSubscriptionTier;
    private int actualProjectCount;

    public Workspace(List<DomainEvent> workspaceDomainEvents) {
        loadFromHistory(workspaceDomainEvents);
    }

    private Workspace(WorkspaceId workspaceId, String workspaceName) {
        validateWorkspaceId(workspaceId);
        validateWorkspaceName(workspaceName);

        raiseEvent(new WorkspaceCreated(workspaceId, workspaceName, SubscriptionTier.FREEMIUM));
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

    public void upgradeSubscriptionTier() {
        if (currentSubscriptionTier == SubscriptionTier.PREMIUM) throw new SubscriptionTierException("Already premium member");
        raiseEvent(new WorkspaceSubscriptionUpgraded(new WorkspaceId(aggregateId), SubscriptionTier.PREMIUM));
    }

    public void approveProjectCreation() {
        if (currentSubscriptionTier == SubscriptionTier.FREEMIUM && actualProjectCount >= 2)
            throw new ProjectLimitReachedException("Workspace has already reached max projects limit for subscription tier.");
        raiseEvent(new WorkspaceProjectCreationApproved(new WorkspaceId(aggregateId)));
    }

    @Override
    protected void apply(DomainEvent event) {
        switch (event) {
            case WorkspaceCreated workspaceCreated -> apply(workspaceCreated);
            case WorkspaceSubscriptionUpgraded workspaceSubscriptionUpgraded -> apply(workspaceSubscriptionUpgraded);
            case WorkspaceProjectCreationApproved workspaceProjectCreationApproved -> apply(workspaceProjectCreationApproved);
            default -> throw new IllegalStateException("Unexpected value: " + event);
        }
    }

    private void apply(WorkspaceCreated workspaceCreated) {
        aggregateId = workspaceCreated.workspaceId().value();
        currentSubscriptionTier = workspaceCreated.subscriptionTier();
    }

    private void apply(WorkspaceSubscriptionUpgraded workspaceSubscriptionUpgraded) {
        this.currentSubscriptionTier = workspaceSubscriptionUpgraded.newSubscriptionTier();
    }

    private void  apply(WorkspaceProjectCreationApproved workspaceProjectCreationApproved) {
        this.actualProjectCount++;
    }

}
