package fr.joffreybonifay.ccpp.shared.event;

import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import java.math.BigDecimal;

public record ProjectCreationRequested(
    ProjectId projectId,
    WorkspaceId workspaceId,
    UserId userId,
    String title,
    BigDecimal projectBudgetLimit
) implements DomainEvent {
}
