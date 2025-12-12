package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.ProjectDomainEvent;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public final class ParticipantAcceptedInvitation extends ProjectDomainEvent {

    private final ParticipantId participantId;

    public ParticipantAcceptedInvitation(ProjectId projectId, ParticipantId participantId, Integer eventSequence) {
        super(projectId, eventSequence);
        this.participantId = participantId;
    }
}
