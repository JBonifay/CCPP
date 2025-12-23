package fr.joffreybonifay.ccpp.projectplanning.domain.event;

import fr.joffreybonifay.ccpp.projectplanning.domain.model.BudgetItem;
import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.BudgetItemId;

public record BudgetItemRemoved(
        ProjectId projectId,
        BudgetItem budgetItem
) implements DomainEvent {

}
