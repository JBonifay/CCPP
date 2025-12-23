package fr.joffreybonifay.ccpp.workspace.application.projection;

import fr.joffreybonifay.ccpp.shared.domain.event.WorkspaceCreated;
import fr.joffreybonifay.ccpp.shared.domain.event.WorkspaceProjectCreationApproved;
import fr.joffreybonifay.ccpp.workspace.domain.event.WorkspaceSubscriptionUpgraded;

public interface WorkspaceProjectionHandler {
    void on(WorkspaceCreated event);
    void on(WorkspaceProjectCreationApproved event);
    void on(WorkspaceSubscriptionUpgraded event);
}
