package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;

public record ProjectDetailsUpdated(
        ProjectId projectId,
        String title,
        String description
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
