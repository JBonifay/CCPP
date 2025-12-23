package fr.joffreybonifay.ccpp.shared.domain.event;

import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.DateRange;

import java.math.BigDecimal;

public record ProjectCreationRequested(
    ProjectId projectId,
    WorkspaceId workspaceId,
    UserId userId,
    String title,
    String description,
    DateRange timeline,
    BigDecimal projectBudgetLimit
) implements DomainEvent {

}
