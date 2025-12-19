package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.DateRange;

import java.util.UUID;

public record ChangeTimelineCommand(
        UUID commandId,
        ProjectId projectId,
        DateRange newTimeline,
        UUID correlationId
)  implements Command {

    public ChangeTimelineCommand(ProjectId projectId, DateRange newTimeline, UUID correlationId) {
        this(UUID.randomUUID(), projectId, newTimeline, correlationId);
    }

    @Override
    public UUID aggregateId() {
        return projectId.value();
    }

    @Override
    public UUID correlationId() {
        return correlationId != null ? correlationId : commandId;
    }

    @Override
    public UUID causationId() {
        return commandId;
    }
}
