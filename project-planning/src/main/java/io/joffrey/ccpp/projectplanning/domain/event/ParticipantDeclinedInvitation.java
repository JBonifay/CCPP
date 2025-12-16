package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.identities.ProjectId;
import com.ccpp.shared.infrastructure.event.DomainEvent;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;

public record ParticipantDeclinedInvitation(
        ProjectId projectId,
        ParticipantId participantId
) implements DomainEvent {
}
