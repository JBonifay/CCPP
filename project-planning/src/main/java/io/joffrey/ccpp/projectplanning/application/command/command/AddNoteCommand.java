package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;

public record AddNoteCommand(
        ProjectId projectId,
        String content,
        UserId userId
)  implements Command {
}
