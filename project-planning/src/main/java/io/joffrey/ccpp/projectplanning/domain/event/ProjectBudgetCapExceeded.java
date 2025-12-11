package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.Money;

public record ProjectBudgetCapExceeded(
        ProjectId projectId,
        Money actualBudget
) implements DomainEvent {

}
