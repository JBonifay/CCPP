package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;

public record ParticipantDeclinedInvitation(
        ProjectId projectId,
        ParticipantId participantId
) implements ProjectDomainEvent {
}
