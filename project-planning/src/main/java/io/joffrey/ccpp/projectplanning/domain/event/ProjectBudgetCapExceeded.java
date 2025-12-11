package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.Money;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ProjectBudgetCapExceeded extends DomainEvent {

    private final ProjectId projectId;
    private final Money actualBudget;

    public ProjectBudgetCapExceeded(ProjectId projectId, Money actualBudget) {
        super();
        this.projectId = projectId;
        this.actualBudget = actualBudget;
    }

}
