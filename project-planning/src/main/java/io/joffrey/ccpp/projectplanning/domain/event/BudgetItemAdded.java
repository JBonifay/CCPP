package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.domain.ProjectDomainEvent;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public final class BudgetItemAdded extends ProjectDomainEvent {

    private final BudgetItemId budgetItemId;
    private final String description;
    private final Money amount;

    public BudgetItemAdded(ProjectId projectId, BudgetItemId budgetItemId, String description, Money amount, Integer eventSequence) {
        super(projectId, eventSequence);
        this.budgetItemId = budgetItemId;
        this.description = description;
        this.amount = amount;
    }
}
