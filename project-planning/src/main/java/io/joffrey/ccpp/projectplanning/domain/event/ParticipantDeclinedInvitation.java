package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ParticipantDeclinedInvitation extends DomainEvent {

    private final ProjectId projectId;
    private final ParticipantId participantId;

    public ParticipantDeclinedInvitation(ProjectId projectId, ParticipantId participantId) {
        super();
        this.projectId = projectId;
        this.participantId = participantId;
    }
}
