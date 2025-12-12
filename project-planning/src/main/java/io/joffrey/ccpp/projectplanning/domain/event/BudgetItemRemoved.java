package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import lombok.Getter;
import lombok.experimental.Accessors;

public record BudgetItemRemoved(
        ProjectId projectId,
        BudgetItemId budgetItemId
) implements ProjectDomainEvent {

}
