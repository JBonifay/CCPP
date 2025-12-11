package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;

public record ParticipantInvited(
        ProjectId projectId,
        ParticipantId participantId,
        String mail,
        String name
) implements DomainEvent {

}
