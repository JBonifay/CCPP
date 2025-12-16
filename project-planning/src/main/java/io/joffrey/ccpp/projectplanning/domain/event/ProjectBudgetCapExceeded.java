package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.identities.ProjectId;
import com.ccpp.shared.domain.valueobjects.Money;
import com.ccpp.shared.infrastructure.event.DomainEvent;

public record ProjectBudgetCapExceeded(
        ProjectId projectId,
        Money actualBudget
) implements DomainEvent {

}
