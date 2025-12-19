package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.Money;
import com.ccpp.shared.event.DomainEvent;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;

public record BudgetItemUpdated(
        ProjectId projectId,
        BudgetItemId budgetItemId,
        String description,
        Money newAmount
) implements DomainEvent {

}
