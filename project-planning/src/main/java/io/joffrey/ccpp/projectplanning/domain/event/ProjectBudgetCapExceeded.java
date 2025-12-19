package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.Money;
import com.ccpp.shared.event.DomainEvent;

public record ProjectBudgetCapExceeded(
        ProjectId projectId,
        Money actualBudget
) implements DomainEvent {

}
