package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;

public record ProjectCreated(
        WorkspaceId workspaceId,
        UserId userId,
        ProjectId projectId,
        String title,
        String description,
        DateRange timeline
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
