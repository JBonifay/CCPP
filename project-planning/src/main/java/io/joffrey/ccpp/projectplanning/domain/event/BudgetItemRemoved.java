package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BudgetItemRemoved extends DomainEvent {

    private final ProjectId projectId;
    private final BudgetItemId budgetItemId;

    public BudgetItemRemoved(ProjectId projectId, BudgetItemId budgetItemId) {
        super();
        this.projectId = projectId;
        this.budgetItemId = budgetItemId;
    }
}
