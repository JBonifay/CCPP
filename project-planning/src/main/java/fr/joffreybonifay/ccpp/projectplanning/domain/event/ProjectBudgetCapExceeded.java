package fr.joffreybonifay.ccpp.projectplanning.domain.event;

import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.valueobjects.Money;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;

public record ProjectBudgetCapExceeded(
        ProjectId projectId,
        Money actualBudget
) implements DomainEvent {

}
