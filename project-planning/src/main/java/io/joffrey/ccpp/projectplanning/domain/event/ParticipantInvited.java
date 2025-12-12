package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.ProjectDomainEvent;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public final class ParticipantInvited extends ProjectDomainEvent {

    private final ParticipantId participantId;
    private final String mail;
    private final String name;

    public ParticipantInvited(ProjectId projectId, ParticipantId participantId, String mail, String name, Integer eventSequence) {
        super(projectId, eventSequence);
        this.participantId = participantId;
        this.mail = mail;
        this.name = name;
    }
}
