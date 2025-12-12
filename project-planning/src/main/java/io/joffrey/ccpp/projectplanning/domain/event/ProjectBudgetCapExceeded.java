package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.Money;
import lombok.Getter;
import lombok.experimental.Accessors;

public record ProjectBudgetCapExceeded(
        ProjectId projectId,
        Money actualBudget
) implements ProjectDomainEvent {

}
