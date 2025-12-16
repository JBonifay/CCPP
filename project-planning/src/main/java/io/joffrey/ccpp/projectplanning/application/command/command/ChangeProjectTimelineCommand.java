package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.infrastructure.command.Command;
import com.ccpp.shared.domain.identities.ProjectId;
import com.ccpp.shared.domain.valueobjects.DateRange;

public record ChangeProjectTimelineCommand(
        ProjectId projectId,
        DateRange newTimeline
)  implements Command {
}
