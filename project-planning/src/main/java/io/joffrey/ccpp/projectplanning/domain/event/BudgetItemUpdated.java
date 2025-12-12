package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.domain.ProjectDomainEvent;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public final class BudgetItemUpdated extends ProjectDomainEvent {

    private final BudgetItemId budgetItemId;
    private final String description;
    private final Money newAmount;

    public BudgetItemUpdated(ProjectId projectId, BudgetItemId budgetItemId, String description, Money newAmount, Integer eventSequence) {
        super(projectId, eventSequence);
        this.budgetItemId = budgetItemId;
        this.description = description;
        this.newAmount = newAmount;
    }
}
