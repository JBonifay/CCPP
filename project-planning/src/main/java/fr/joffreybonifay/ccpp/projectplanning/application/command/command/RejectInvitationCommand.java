package fr.joffreybonifay.ccpp.projectplanning.application.command.command;

import fr.joffreybonifay.ccpp.shared.command.Command;
import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.ParticipantId;

import java.util.UUID;

public record RejectInvitationCommand(
        UUID commandId,
        ProjectId projectId,
        ParticipantId participantId,
        UUID correlationId
)  implements Command {

    public RejectInvitationCommand(ProjectId projectId, ParticipantId participantId, UUID correlationId) {
        this(UUID.randomUUID(), projectId, participantId, correlationId);
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
