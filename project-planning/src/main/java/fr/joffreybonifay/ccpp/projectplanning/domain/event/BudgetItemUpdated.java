package fr.joffreybonifay.ccpp.projectplanning.domain.event;

import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.Money;
import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.BudgetItemId;

public record BudgetItemUpdated(
        ProjectId projectId,
        BudgetItemId budgetItemId,
        String description,
        Money oldAmount,
        Money newAmount
) implements DomainEvent {

}
