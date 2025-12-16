package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.identities.ProjectId;
import com.ccpp.shared.domain.valueobjects.Money;
import com.ccpp.shared.infrastructure.event.DomainEvent;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;

public record BudgetItemAdded(
        ProjectId projectId,
        BudgetItemId budgetItemId,
        String description,
        Money amount
) implements DomainEvent {

}
