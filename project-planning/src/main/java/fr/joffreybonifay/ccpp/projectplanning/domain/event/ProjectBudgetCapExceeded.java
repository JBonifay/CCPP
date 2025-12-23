package fr.joffreybonifay.ccpp.projectplanning.domain.event;

import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.Money;
import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;

public record ProjectBudgetCapExceeded(
        ProjectId projectId,
        Money actualBudget
) implements DomainEvent {

}
