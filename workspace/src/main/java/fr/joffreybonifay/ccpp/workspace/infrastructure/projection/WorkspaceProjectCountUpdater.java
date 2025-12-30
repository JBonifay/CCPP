package fr.joffreybonifay.ccpp.workspace.infrastructure.projection;

import fr.joffreybonifay.ccpp.shared.domain.event.WorkspaceCreated;
import fr.joffreybonifay.ccpp.shared.domain.event.WorkspaceProjectCreationApproved;
import fr.joffreybonifay.ccpp.workspace.application.projection.WorkspaceProjectCountProjection;
import fr.joffreybonifay.ccpp.workspace.application.query.model.WorkspaceProjectCountDTO;
import fr.joffreybonifay.ccpp.workspace.application.query.repository.WorkspaceProjectCountReadRepository;
import fr.joffreybonifay.ccpp.workspace.domain.event.WorkspaceSubscriptionUpgraded;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceProjectCountUpdater implements WorkspaceProjectCountProjection {

    private final WorkspaceProjectCountReadRepository repository;

    public WorkspaceProjectCountUpdater(WorkspaceProjectCountReadRepository repository) {
        this.repository = repository;
    }

    @Override
    public void on(WorkspaceCreated event) {
        var dto = new WorkspaceProjectCountDTO(
                event.workspaceId(),
                event.logoUrl(),
                0,
                event.subscriptionTier()
        );
        repository.save(dto);
    }

    @Override
    public void on(WorkspaceProjectCreationApproved event) {
        var current = repository.findById(event.workspaceId())
                .orElseThrow(() -> new IllegalStateException(
                        "Projection missing for workspace " + event.workspaceId()
                ));

        repository.save(new WorkspaceProjectCountDTO(
                current.workspaceId(),
                current.logoUrl(),
                current.projectCount() + 1,
                current.subscriptionTier()
        ));
    }

    @Override
    public void on(WorkspaceSubscriptionUpgraded event) {
        var current = repository.findById(event.workspaceId())
                .orElseThrow(() -> new IllegalStateException(
                        "Projection missing for workspace " + event.workspaceId()
                ));

        repository.save(new WorkspaceProjectCountDTO(
                current.workspaceId(),
                current.logoUrl(),
                current.projectCount(),
                event.newSubscriptionTier()
        ));
    }

}
