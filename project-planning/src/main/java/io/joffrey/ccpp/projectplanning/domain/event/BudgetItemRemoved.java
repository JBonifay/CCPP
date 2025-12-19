package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.event.DomainEvent;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;

public record BudgetItemRemoved(
        ProjectId projectId,
        BudgetItemId budgetItemId
) implements DomainEvent {

}
