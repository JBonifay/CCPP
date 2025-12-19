package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.ProjectId;

import java.util.UUID;

public record UpdateProjectDetailsCommand(
        ProjectId projectId,
        String title,
        String description
)  implements Command {
    @Override
    public UUID getCommandId() {
        return null;
    }

    @Override
    public UUID getAggregateId() {
        return null;
    }

    @Override
    public UUID getCorrelationId() {
        return null;
    }

    @Override
    public UUID getCausationId() {
        return null;
    }
}
