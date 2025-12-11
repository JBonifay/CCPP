package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectDetailsUpdated extends DomainEvent {

    private final ProjectId projectId;
    private final String title;
    private final String description;

    public ProjectDetailsUpdated(ProjectId projectId, String title, String description) {
        super();
        this.projectId = projectId;
        this.title = title;
        this.description = description;
    }
}
