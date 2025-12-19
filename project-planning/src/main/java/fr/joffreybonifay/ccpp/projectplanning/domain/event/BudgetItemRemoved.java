package fr.joffreybonifay.ccpp.projectplanning.domain.event;

import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.BudgetItemId;

public record BudgetItemRemoved(
        ProjectId projectId,
        BudgetItemId budgetItemId
) implements DomainEvent {

}
