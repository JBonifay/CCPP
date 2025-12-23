package fr.joffreybonifay.ccpp.projectplanning.application.command.command;

import fr.joffreybonifay.ccpp.shared.command.Command;
import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.DateRange;

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
    public UUID correlationId() {
        return correlationId != null ? correlationId : commandId;
    }

    @Override
    public UUID causationId() {
        return commandId;
    }
}
