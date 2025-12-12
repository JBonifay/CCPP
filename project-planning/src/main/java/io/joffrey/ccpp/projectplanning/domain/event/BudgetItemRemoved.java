package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.ProjectDomainEvent;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public final class BudgetItemRemoved extends ProjectDomainEvent {

    private final BudgetItemId budgetItemId;

    public BudgetItemRemoved(ProjectId projectId, BudgetItemId budgetItemId, Integer eventSequence) {
        super(projectId, eventSequence);
        this.budgetItemId = budgetItemId;
    }
}
