package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import lombok.Getter;
import lombok.experimental.Accessors;

public record BudgetItemAdded(
        ProjectId projectId,
        BudgetItemId budgetItemId,
        String description,
        Money amount
) implements ProjectDomainEvent {

}
