package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.event.DomainEvent;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;


public record ParticipantAcceptedInvitation(
        ProjectId projectId,
        ParticipantId participantId
) implements DomainEvent {

}
