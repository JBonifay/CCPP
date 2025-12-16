package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.infrastructure.command.Command;
import com.ccpp.shared.domain.identities.ProjectId;
import com.ccpp.shared.domain.identities.UserId;

public record AddNoteCommand(
        ProjectId projectId,
        String content,
        UserId userId
)  implements Command {
}
