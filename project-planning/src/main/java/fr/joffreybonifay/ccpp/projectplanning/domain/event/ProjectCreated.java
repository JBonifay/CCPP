package fr.joffreybonifay.ccpp.projectplanning.domain.event;

import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.valueobjects.DateRange;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;

import java.math.BigDecimal;

public record ProjectCreated(
    ProjectId projectId,
    WorkspaceId workspaceId,
    UserId userId,
    String title,
    String description,
    DateRange timeline,
    BigDecimal projectBudgetLimit
) implements DomainEvent {

}
