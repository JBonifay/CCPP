package io.joffrey.ccpp.workspace.infrastructure.projection;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.domain.EventListener;
import io.joffrey.ccpp.workspace.application.query.model.WorkspaceProjectCountDTO;
import io.joffrey.ccpp.workspace.application.query.repository.WorkspaceProjectCountReadRepository;
import io.joffrey.ccpp.workspace.domain.event.WorkspaceCreated;
import io.joffrey.ccpp.workspace.domain.event.WorkspaceDomainEvent;
import io.joffrey.ccpp.workspace.domain.event.WorkspaceProjectCreationApproved;
import io.joffrey.ccpp.workspace.domain.event.WorkspaceSubscriptionUpgraded;
import io.joffrey.ccpp.workspace.domain.model.Membership;

public class WorkspaceProjectCountProjectionUpdater implements EventListener {

    private final WorkspaceProjectCountReadRepository repository;

    public WorkspaceProjectCountProjectionUpdater(WorkspaceProjectCountReadRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean canHandle(DomainEvent event) {
        return event instanceof WorkspaceDomainEvent;
    }

    @Override
    public void onEvent(DomainEvent event) {
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
                workspaceCreated.membership()
        );
        repository.save(dto);
    }

    private void handleWorkspaceProjectCreationApproved(WorkspaceProjectCreationApproved workspaceProjectCreationApproved) {
        repository.findById(workspaceProjectCreationApproved.workspaceId()).ifPresent(current -> {
            var dto = new WorkspaceProjectCountDTO(
                    current.workspaceId(),
                    current.projectCount() + 1,
                    current.membership()
            );
            repository.save(dto);
        });
    }

    private void handlerWorkspaceSubscriptionUpgraded(WorkspaceSubscriptionUpgraded workspaceSubscriptionUpgraded) {
        repository.findById(workspaceSubscriptionUpgraded.workspaceId()).ifPresent(current -> {
            var dto = new WorkspaceProjectCountDTO(
                    current.workspaceId(),
                    current.projectCount(),
                    Membership.PREMIUM
            );
            repository.save(dto);
        });
    }

}
