package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.ProjectDomainEvent;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public final class ParticipantDeclinedInvitation extends ProjectDomainEvent {

    private final ParticipantId participantId;

    public ParticipantDeclinedInvitation(ProjectId projectId, ParticipantId participantId, Integer eventSequence) {
        super(projectId, eventSequence);
        this.participantId = participantId;
    }
}
