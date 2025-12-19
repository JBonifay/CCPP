package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;

import java.util.UUID;

public record AddNoteCommand(
        UUID commandId,
        ProjectId projectId,
        String content,
        UserId userId,
        UUID correlationId,
        UUID causationId
)  implements Command {

    @Override
    public UUID aggregateId() {
        return null;
    }
}
