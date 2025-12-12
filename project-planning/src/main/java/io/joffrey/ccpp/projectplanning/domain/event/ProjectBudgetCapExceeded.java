package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.domain.ProjectDomainEvent;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public final class ProjectBudgetCapExceeded extends ProjectDomainEvent {

    private final Money actualBudget;

    public ProjectBudgetCapExceeded(ProjectId projectId, Money actualBudget, Integer eventSequence) {
        super(projectId, eventSequence);
        this.actualBudget = actualBudget;
    }
}
