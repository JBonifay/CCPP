package io.joffrey.ccpp.projectplanning.application.command.command;

import com.ccpp.shared.command.Command;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.DateRange;

public record ChangeProjectTimelineCommand(
        ProjectId projectId,
        DateRange newTimeline
)  implements Command {
}
