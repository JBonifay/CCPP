package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;

public record MarkProjectAsReadyCommand(
        ProjectId projectId,
        UserId userId
) {
}
