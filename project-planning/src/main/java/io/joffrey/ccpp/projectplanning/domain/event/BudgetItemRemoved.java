package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;

public record BudgetItemRemoved(
        ProjectId projectId,
        BudgetItemId budgetItemId
) implements DomainEvent {
    @Override
    public void getEventId() {

    }

    @Override
    public void getEventType() {

    }

    @Override
    public void getAggregateId() {

    }

    @Override
    public void getWorkspaceId() {

    }

    @Override
    public void getTimestamp() {

    }

    @Override
    public void getVersion() {

    }
}
