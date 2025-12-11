package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BudgetItemAdded extends DomainEvent {

    private final ProjectId projectId;
    private final BudgetItemId budgetItemId;
    private final String description;
    private final Money amount;

    public BudgetItemAdded(ProjectId projectId, BudgetItemId budgetItemId, String description, Money amount) {
        this.projectId = projectId;
        this.budgetItemId = budgetItemId;
        this.description = description;
        this.amount = amount;
    }

}
