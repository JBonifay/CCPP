package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.DateRange;

import java.util.UUID;

public record ChangeProjectTimelineCommand(
        UUID commandId,
        ProjectId projectId,
        DateRange newTimeline,
        UUID correlationId,
        UUID causationId
)  implements Command {

    @Override
    public UUID aggregateId() {
        return null;
    }
}
