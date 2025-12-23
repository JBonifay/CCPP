package fr.joffreybonifay.ccpp.workspace.domain;

import fr.joffreybonifay.ccpp.shared.domain.AggregateRoot;
import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.domain.event.WorkspaceProjectCreationApproved;
import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.domain.event.WorkspaceCreated;
import fr.joffreybonifay.ccpp.workspace.domain.event.WorkspaceProjectLimitReached;
import fr.joffreybonifay.ccpp.workspace.domain.event.WorkspaceSubscriptionUpgraded;
import fr.joffreybonifay.ccpp.workspace.domain.exception.InvalidWorkspaceDataException;
import fr.joffreybonifay.ccpp.workspace.domain.exception.ProjectLimitReachedException;
import fr.joffreybonifay.ccpp.workspace.domain.exception.SubscriptionTierException;
import fr.joffreybonifay.ccpp.workspace.domain.exception.WorkspaceDoesNotExistException;
import fr.joffreybonifay.ccpp.shared.domain.model.SubscriptionTier;

import java.util.List;

public class Workspace extends AggregateRoot {

    public static final int FREEMIUM_PROJECT_LIMIT = 2;
    private SubscriptionTier currentSubscriptionTier;
    private int actualProjectCount;

    public Workspace(List<DomainEvent> workspaceDomainEvents) {
        if (workspaceDomainEvents.isEmpty()) throw new WorkspaceDoesNotExistException("Workspace does not exists");
        loadFromHistory(workspaceDomainEvents);
    }

    private Workspace(WorkspaceId workspaceId, UserId userId, String workspaceName) {
        validateWorkspaceId(workspaceId);
        validateUserId(userId);
        validateWorkspaceName(workspaceName);

        raiseEvent(new WorkspaceCreated(workspaceId, userId, workspaceName, SubscriptionTier.FREEMIUM));
    }

    public static Workspace create(WorkspaceId workspaceId, UserId userId, String workspaceName) {
        return new Workspace(workspaceId, userId, workspaceName);
    }

    public static Workspace fromHistory(List<DomainEvent> workspaceEvents) {
        return new Workspace(workspaceEvents);
    }

    private void validateWorkspaceId(WorkspaceId workspaceId) {
        if (workspaceId == null || workspaceId.value() == null)
            throw new InvalidWorkspaceDataException("Workspace id cannot be empty");
    }

    private void validateUserId(UserId userId) {
        if (userId == null || userId.value() == null)
            throw new InvalidWorkspaceDataException("User id cannot be empty");
    }

    private void validateWorkspaceName(String workspaceName) {
        if (workspaceName == null || workspaceName.isBlank())
            throw new InvalidWorkspaceDataException("Workspace name cannot be empty");
    }

    public void upgradeSubscriptionTier() {
        if (currentSubscriptionTier == SubscriptionTier.PREMIUM)
            throw new SubscriptionTierException("Already premium member");
        raiseEvent(new WorkspaceSubscriptionUpgraded(new WorkspaceId(aggregateId), SubscriptionTier.PREMIUM));
    }

    public void approveProjectCreation(ProjectId projectId) {
        if (currentSubscriptionTier == SubscriptionTier.FREEMIUM && actualProjectCount >= FREEMIUM_PROJECT_LIMIT) {
            throw new ProjectLimitReachedException("Workspace has already reached max projects limit for subscription tier.");
        }
        raiseEvent(new WorkspaceProjectCreationApproved(new WorkspaceId(aggregateId), projectId));
        if (actualProjectCount >= FREEMIUM_PROJECT_LIMIT) {
            raiseEvent(new WorkspaceProjectLimitReached(new WorkspaceId(aggregateId)));
        }
    }

    @Override
    protected void apply(DomainEvent event) {
        switch (event) {
            case WorkspaceCreated workspaceCreated -> apply(workspaceCreated);
            case WorkspaceSubscriptionUpgraded workspaceSubscriptionUpgraded -> apply(workspaceSubscriptionUpgraded);
            case WorkspaceProjectCreationApproved workspaceProjectCreationApproved ->
                    apply(workspaceProjectCreationApproved);
            case WorkspaceProjectLimitReached workspaceProjectLimitReached -> apply(workspaceProjectLimitReached);
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

    private void apply(WorkspaceProjectCreationApproved workspaceProjectCreationApproved) {
        this.actualProjectCount++;
    }

    private void apply(WorkspaceProjectLimitReached workspaceProjectLimitReached) {

    }

}
