package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.ProjectDomainEvent;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public final class ProjectDetailsUpdated extends ProjectDomainEvent {

    private final String title;
    private final String description;

    public ProjectDetailsUpdated(ProjectId projectId, String title, String description, Integer eventSequence) {
        super(projectId, eventSequence);
        this.title = title;
        this.description = description;
    }
}
