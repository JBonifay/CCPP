package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.ProjectId;

import java.util.UUID;

public record UpdateProjectDetailsCommand(
        UUID commandId,
        ProjectId projectId,
        String title,
        String description,
        UUID correlationId,
        UUID causationId
)  implements Command {

    @Override
    public UUID aggregateId() {
        return null;
    }
}
