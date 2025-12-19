package fr.joffreybonifay.ccpp.workspace.infrastructure.projection;

import fr.joffreybonifay.ccpp.shared.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.eventhandler.EventHandler;
import fr.joffreybonifay.ccpp.workspace.application.query.model.WorkspaceProjectCountDTO;
import fr.joffreybonifay.ccpp.workspace.application.query.repository.WorkspaceProjectCountReadRepository;
import fr.joffreybonifay.ccpp.workspace.domain.event.WorkspaceCreated;
import fr.joffreybonifay.ccpp.workspace.domain.event.WorkspaceProjectCreationApproved;
import fr.joffreybonifay.ccpp.workspace.domain.event.WorkspaceSubscriptionUpgraded;
import fr.joffreybonifay.ccpp.workspace.domain.model.SubscriptionTier;

public class WorkspaceProjectCountProjectionUpdater implements EventHandler {

    private final WorkspaceProjectCountReadRepository repository;

    public WorkspaceProjectCountProjectionUpdater(WorkspaceProjectCountReadRepository repository) {
        this.repository = repository;
    }

    @Override
    public void handle(DomainEvent event) {
        switch (event) {
            case WorkspaceCreated workspaceCreated -> handleWorkspaceCreated(workspaceCreated);
            case WorkspaceProjectCreationApproved approved -> handleWorkspaceProjectCreationApproved(approved);
            case WorkspaceSubscriptionUpgraded upgraded -> handlerWorkspaceSubscriptionUpgraded(upgraded);
            default -> {
            }
        }
    }

    private void handleWorkspaceCreated(WorkspaceCreated workspaceCreated) {
        var dto = new WorkspaceProjectCountDTO(
                workspaceCreated.workspaceId(),
                0,
                workspaceCreated.subscriptionTier()
        );
        repository.save(dto);
    }

    private void handleWorkspaceProjectCreationApproved(WorkspaceProjectCreationApproved workspaceProjectCreationApproved) {
        repository.findById(workspaceProjectCreationApproved.workspaceId()).ifPresent(current -> {
            var dto = new WorkspaceProjectCountDTO(
                    current.workspaceId(),
                    current.projectCount() + 1,
                    current.subscriptionTier()
            );
            repository.save(dto);
        });
    }

    private void handlerWorkspaceSubscriptionUpgraded(WorkspaceSubscriptionUpgraded workspaceSubscriptionUpgraded) {
        repository.findById(workspaceSubscriptionUpgraded.workspaceId()).ifPresent(current -> {
            var dto = new WorkspaceProjectCountDTO(
                    current.workspaceId(),
                    current.projectCount(),
                    SubscriptionTier.PREMIUM
            );
            repository.save(dto);
        });
    }

}
