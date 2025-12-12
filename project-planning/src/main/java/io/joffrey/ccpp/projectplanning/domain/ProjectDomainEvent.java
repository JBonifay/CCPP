package io.joffrey.ccpp.projectplanning.domain;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class ProjectDomainEvent extends DomainEvent {

    private final ProjectId projectId;

    public ProjectDomainEvent(ProjectId projectId, Integer eventSequence) {
        super(eventSequence);
        this.projectId = projectId;
    }

}
