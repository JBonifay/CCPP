package fr.joffreybonifay.ccpp.projectplanning.domain.event;

import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.ParticipantId;


public record ParticipantAcceptedInvitation(
        ProjectId projectId,
        ParticipantId participantId
) implements DomainEvent {

}
